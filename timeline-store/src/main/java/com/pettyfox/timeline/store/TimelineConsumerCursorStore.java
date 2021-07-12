package com.pettyfox.timeline.store;


import com.pettyfox.timeline.model.TimelineHead;
import com.pettyfox.timeline.model.TimelinePullParameter;

import java.util.List;

/**
 * timlineMq游标存储器
 * @author Petty Fox
 * @version 1.0
 * @date 2021/6/17 10:54
 */
public interface TimelineConsumerCursorStore {
    /**
     * 保存消费进度
     * @param consumerId 消费者id
     * @param timelineHead 消息(仅会存储id最大的进度)
     */
    void storeConsumerAck(String consumerId, TimelineHead timelineHead);

    /**
     * 列出消费者进度
     * @param consumerId 消费者id
     * @param producerIds 生产者id列表
     * @return
     */
    List<TimelinePullParameter> listConsumerCursor(String consumerId, List<String> producerIds);

    /**
     * 移除消费者
     * @param consumerId 消费者id
     */
    void removeConsumer(String consumerId);
}
