package com.pettyfox.timeline.strategy;


/**
 * Timeline消息队列游标策略
 * @author Petty Fox
 * @version 1.0
 * @date 2021/6/30 15:37
 */
public interface TimelineMqCursorStrategy {
    /**
     * 消费者消费超时后是否等待
     * @return 如果是true，
     */
    boolean timeoutBlock(String consumerId);
}
