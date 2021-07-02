package org.pettyfox.timeline2.store.impl;

import lombok.extern.slf4j.Slf4j;
import org.pettyfox.timeline2.model.TimelineHead;
import org.pettyfox.timeline2.model.TimelinePullParameter;
import org.pettyfox.timeline2.store.TimelineConsumerCursorStore;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/6/17 13:55
 */
@Slf4j
public class TimelineConsumerCursorStoreMemoryImpl implements TimelineConsumerCursorStore {
    /**
     * 消费者Id，生产者Id，消费游标
     */
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Long>> store = new ConcurrentHashMap<>();

    @Override
    public void storeConsumerAck(String consumerId, TimelineHead timelineHead) {
        ConcurrentHashMap<String, Long> temp = store.computeIfAbsent(consumerId, k -> new ConcurrentHashMap<>(10));
        synchronized (consumerId.intern()) {
            if (temp.containsKey(timelineHead.getTopic())) {
                if (temp.get(timelineHead.getTopic()) < timelineHead.getId()) {
                    temp.put(timelineHead.getTopic(), timelineHead.getId());
                }
            } else {
                temp.put(timelineHead.getTopic(), timelineHead.getId());
            }
        }
//        log.info("cursor:{},{},{}", consumerId, timelineHead.getTopic(), temp.get(timelineHead.getTopic()));

    }

    @Override
    public List<TimelinePullParameter> listConsumerCursor(String consumerId, List<String> producerIds) {
        return producerIds.stream().map(p -> {
            TimelinePullParameter parameter = new TimelinePullParameter();
            parameter.setTopic(p);
            if (store.containsKey(consumerId)) {
                synchronized (consumerId.intern()) {
                    parameter.setCursorFrom(Optional.ofNullable(store.get(consumerId).get(p)).orElse(0L));
//                    log.info("get cursor:{},{}", consumerId,parameter.getCursorFrom());
                }
            }
            return parameter;
        }).collect(Collectors.toList());
    }
}
