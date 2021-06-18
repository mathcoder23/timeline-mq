package org.pettyfox.timeline2.strategy.fanout;


import org.pettyfox.timeline2.core.TimelineMq;
import org.pettyfox.timeline2.model.TimelineMessage;
import org.pettyfox.timeline2.model.TimelinePullParameter;
import org.pettyfox.timeline2.store.TimelineMqStore;

import java.util.List;

/**
 * Timeline 服务，提供对外接口
 *
 * @author Petty Fox
 * @version 1.0
 */
public class TimelineMqFanoutImpl implements TimelineMq {
    private final TimelineMqStore timelineStore;

    public TimelineMqFanoutImpl(TimelineMqStore timelineStore) {
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
