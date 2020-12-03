package org.pettyfox.timeline.core;

import org.pettyfox.timeline.strategy.status.TimelineMessageStatusFanout;
import org.pettyfox.timeline.strategy.status.TimelineMqStatusFanoutImpl;

/**
 * @author Petty Fox
 * @version 1.0
 */
public class TimelineMqFactory {
    public static <T extends TimelineMessageStatusFanout> TimelineMq<T> createMqStatusFanout(TimelineStore<T> store, TimelineExchange exchange){
        return new TimelineMqStatusFanoutImpl<T>(store, exchange);
    }
}
