package com.pettyfox.timeline.core;

import com.pettyfox.timeline.store.TimelineConsumerCursorStore;
import com.pettyfox.timeline.store.TimelineExchangeStore;
import com.pettyfox.timeline.store.TimelineMqStore;
import com.pettyfox.timeline.strategy.TimelineMqCursorImpl;
import com.pettyfox.timeline.strategy.TimelineMqDefaultImpl;

/**
 * @author Petty Fox
 * @version 1.0
 */
public class TimelineMqFactory {
    public static TimelineMq createDefault(TimelineMqStore timelineStore) {
        return new TimelineMqDefaultImpl(timelineStore);
    }

    public static TimelineCursorMq createCursorMq(TimelineMqStore timelineStore, TimelineExchangeStore timelineExchange, TimelineConsumerCursorStore timelineConsumerCursorStore) {
        return new TimelineMqCursorImpl(timelineStore, timelineExchange, timelineConsumerCursorStore);
    }
}
