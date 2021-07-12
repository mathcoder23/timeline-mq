package com.pettyfox.timeline.strategy;


import com.pettyfox.timeline.core.TimelineMq;
import com.pettyfox.timeline.model.TimelineMessage;
import com.pettyfox.timeline.model.TimelinePullParameter;
import com.pettyfox.timeline.store.TimelineMqStore;

import java.util.List;

/**
 * Timeline 服务，提供对外接口
 *
 * @author Petty Fox
 * @version 1.0
 */
public class TimelineMqDefaultImpl implements TimelineMq {
    private final TimelineMqStore timelineStore;

    public TimelineMqDefaultImpl(TimelineMqStore timelineStore) {
        this.timelineStore = timelineStore;
    }

    @Override
    public void push(TimelineMessage timelineMessage) {
        timelineStore.store(timelineMessage);
    }

    @Override
    public List<TimelineMessage> pull(TimelinePullParameter... parameter) {
        return timelineStore.listTimeline(parameter);
    }
}
