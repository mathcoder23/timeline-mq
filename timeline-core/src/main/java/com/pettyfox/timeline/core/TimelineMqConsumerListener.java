package com.pettyfox.timeline.core;

import com.pettyfox.timeline.model.TimelineMessage;

import java.util.List;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/6/18 10:05
 */
public interface TimelineMqConsumerListener {
    /**
     * 批量消费
     * @param queue 队列
     */
    void batchConsumer(List<TimelineMessage> queue,String consumerId);
}
