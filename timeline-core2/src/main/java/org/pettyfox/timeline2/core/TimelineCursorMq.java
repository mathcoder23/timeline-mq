package org.pettyfox.timeline2.core;

import org.pettyfox.timeline2.model.TimelineHead;
import org.pettyfox.timeline2.model.TimelineMessage;

import java.util.List;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/6/17 11:05
 */
public interface TimelineCursorMq {
    /**
     * 推入消息
     *
     * @param timelineMessage 消息体
     */
    void push(TimelineMessage timelineMessage);

    /**
     * 消费这Ack确认
     *
     * @param consumerId   消费者id
     * @param timelineHead 消息头
     */
    void consumerAck(String consumerId, TimelineHead timelineHead);

    /**
     * 拉取消息
     *
     * @return 消息队列
     */
    List<TimelineMessage> pull(String consumerId, int batchSize);

    /**
     * 注册消费者
     *
     * @param consumerId 消费者id
     * @param batchSize  一次消费批
     */
    void registerConsumer(String consumerId, int batchSize, TimelineMqConsumerListener listener);

    /**
     * 取消消费者
     *
     * @param consumerId 消费者id
     */
    void unregisterConsumer(String consumerId);

    void setTimeoutListener(TimelineMqConsumerTimeout timeoutListener);

    /**
     * 唤醒消费者
     * @param consumerId
     */
    void wakeupConsumer(String consumerId);
}
