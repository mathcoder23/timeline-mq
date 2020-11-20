package org.pettyfox.timeline.core;

import org.pettyfox.timeline.store.TimelineExchangeMemoryImpl;

/**
 * @author Petty Fox
 * @version 1.0
 */
public class TimelineExchangeFactory {
    public static TimelineExchange createExchangeMemory(){
        return new TimelineExchangeMemoryImpl();
    }
}
