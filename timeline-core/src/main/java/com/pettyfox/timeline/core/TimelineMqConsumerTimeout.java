package com.pettyfox.timeline.core;

import com.pettyfox.timeline.model.TimelineHead;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/6/21 14:15
 */
public interface TimelineMqConsumerTimeout {
    boolean timeout(String consumerId, TimelineHead timelineHead);
}
