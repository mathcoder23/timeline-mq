package org.pettyfox.timeline.store;

import org.pettyfox.timeline.core.TimelineStore;
import org.pettyfox.timeline.model.Timeline;
import org.pettyfox.timeline.model.TimelineMessage;
import org.pettyfox.timeline.model.TimelinePullParameter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Petty Fox
 * @version 1.0
 */
public class TimelineStoreMemoryImpl<T extends TimelineMessage> implements TimelineStore<T> {
    private ConcurrentHashMap<String,List<Timeline<T>>> map = new ConcurrentHashMap<>();

    @Override
    public void store(Timeline<T> timeline) {
        map.computeIfAbsent(timeline.getProducerId(),k->new ArrayList<>()).add(timeline);
    }

    @Override
    public List<Timeline<T>> listTimeline(List<String> producerIds, TimelinePullParameter parameter) {
        List<Timeline<T>> timelines = new ArrayList<>();
        producerIds.forEach(producerId->{
            timelines.addAll( map.get(producerId).stream()
                    .filter(timeline -> timeline.getSequenceId() >= parameter.getFrom() && timeline.getSequenceId() <= parameter.getTo())
                    .collect(Collectors.toList()));
        });
        return timelines.stream().sorted(Comparator.comparing(Timeline::getSequenceId)).limit(parameter.getBatchSize()).collect(Collectors.toList());
    }
}
