package org.pettyfox.timeline2.core;

import org.pettyfox.timeline2.store.TimelineExchangeMemoryImpl;

/**
 * @author Petty Fox
 * @version 1.0
 */
public class TimelineExchangeFactory {
    public static TimelineExchange createExchangeMemory(){
        return new TimelineExchangeMemoryImpl();
    }
}
