package org.pettyfox.timeline.core;

import org.pettyfox.timeline.strategy.status.TimelineMessageStatusFanout;
import org.pettyfox.timeline.strategy.status.TimelineMqStatusFanoutImpl;

/**
 * @author Petty Fox
 * @version 1.0
 */
public class TimelineMqFactory {
    public static TimelineMq<TimelineMessageStatusFanout> createMqStatusFanout(TimelineStore store, TimelineExchange exchange){
        return new TimelineMqStatusFanoutImpl<>(store, exchange);
    }
}
