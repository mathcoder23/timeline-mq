package org.pettyfox.timeline2.core;

import org.pettyfox.timeline2.model.TimelineHead;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/6/21 14:15
 */
public interface TimelineMqConsumerTimeout {
    void timeout(String consumerId, TimelineHead timelineHead);
}
