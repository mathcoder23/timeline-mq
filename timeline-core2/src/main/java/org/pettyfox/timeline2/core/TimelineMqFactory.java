package org.pettyfox.timeline2.core;

import org.pettyfox.timeline2.store.TimelineConsumerCursorStore;
import org.pettyfox.timeline2.store.TimelineExchange;
import org.pettyfox.timeline2.store.TimelineMqStore;
import org.pettyfox.timeline2.strategy.TimelineMqCursorImpl;
import org.pettyfox.timeline2.strategy.TimelineMqDefaultImpl;

/**
 * @author Petty Fox
 * @version 1.0
 */
public class TimelineMqFactory {
    public static TimelineMq createDefault(TimelineMqStore timelineStore) {
        return new TimelineMqDefaultImpl(timelineStore);
    }

    public static TimelineCursorMq createCursorMq(TimelineMqStore timelineStore, TimelineExchange timelineExchange, TimelineConsumerCursorStore timelineConsumerCursorStore) {
        return new TimelineMqCursorImpl(timelineStore, timelineExchange, timelineConsumerCursorStore);
    }
}
