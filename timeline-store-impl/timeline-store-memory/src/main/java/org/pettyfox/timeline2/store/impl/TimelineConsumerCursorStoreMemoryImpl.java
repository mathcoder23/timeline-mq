package org.pettyfox.timeline2.store.impl;

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
public class TimelineConsumerCursorStoreMemoryImpl implements TimelineConsumerCursorStore {
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Long>> store = new ConcurrentHashMap<>();

    @Override
    public void storeConsumerAck(String consumerId, TimelineHead timelineHead) {
        ConcurrentHashMap<String, Long> temp = store.computeIfAbsent(consumerId, k -> new ConcurrentHashMap<>());
        temp.put(timelineHead.getTopic(), timelineHead.getId());
    }

    @Override
    public List<TimelinePullParameter> listConsumerCursor(String consumerId, List<String> producerIds) {
        return producerIds.stream().map(p -> {
            TimelinePullParameter parameter = new TimelinePullParameter();
            parameter.setTopic(p);
            if (store.containsKey(consumerId)) {
                parameter.setCursorFrom(Optional.ofNullable(store.get(consumerId).get(p)).orElse(0L));
            }
            return parameter;
        }).collect(Collectors.toList());
    }
}
