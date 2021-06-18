package org.pettyfox.timeline2.strategy.status;

import org.pettyfox.timeline2.core.TimelineExchange;
import org.pettyfox.timeline2.core.TimelineMq;
import org.pettyfox.timeline2.core.TimelineStore;
import org.pettyfox.timeline2.model.Timeline;
import org.pettyfox.timeline2.model.TimelinePullParameter;

import java.util.List;

/**
 * @author Petty Fox
 * @version 1.0
 */
public class TimelineMqStatusFanoutImpl<T extends TimelineMessageStatusFanout> implements TimelineMq<T> {
    private TimelineStore<T> store;
    private TimelineExchange exchange;
    public TimelineMqStatusFanoutImpl(TimelineStore<T> store, TimelineExchange exchange){
        this.store = store;
        this.exchange = exchange;
    }
    @Override
    public void push(Timeline<T> timeline) {
        this.store.store(timeline);
    }

    @Override
    public List<Timeline<T>> pull(TimelinePullParameter parameter) {
        return this.store.listTimeline(exchange.listByBeSubscribe(parameter.getConsumerId()),parameter);
    }
}
