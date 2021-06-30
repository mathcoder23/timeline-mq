package org.pettyfox.timeline2.store.impl;


import org.pettyfox.timeline2.model.TimelineMessage;
import org.pettyfox.timeline2.model.TimelinePullParameter;
import org.pettyfox.timeline2.store.TimelineMqStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 基于内存的消息存储
 *
 * @author Petty Fox
 * @version 1.0
 */
public class TimelineMqStoreMemoryImpl implements TimelineMqStore {
    private ConcurrentHashMap<String, List<TimelineMessage>> store = new ConcurrentHashMap<>();

    @Override
    public void store(TimelineMessage timelineMessage) {
        List<TimelineMessage> list = store.computeIfAbsent(timelineMessage.getTopic(), (key) -> new CopyOnWriteArrayList<>());
        list.add(timelineMessage);
    }

    @Override
    public List<TimelineMessage> listTimeline(TimelinePullParameter... parameter) {
        return listTimeline(Arrays.asList(parameter));
    }

    @Override
    public List<TimelineMessage> listTimeline(List<TimelinePullParameter> parameter) {
        List<TimelineMessage> result = new ArrayList<>();
        for (TimelinePullParameter p : parameter) {
            if (!store.containsKey(p.getTopic())) {
                continue;
            }
            List<TimelineMessage> tempList = store.get(p.getTopic())
                    .stream().filter(t -> t.getId() > p.getCursorFrom() && t.getId() <= p.getCursorTo())
                    .limit(p.getBatchSize())
                    .collect(Collectors.toList());
            if (tempList.size() > 0) {
                result.addAll(tempList);
            }
            if (result.size() >= p.getBatchSize()) {
                //TODO 排序后切割
                return result;
            }
        }
        return result;
    }
}
