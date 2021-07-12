package com.pettyfox.timeline.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Petty Fox
 * @version 1.0
 */
@Setter
@Getter
public class TimelinePullParameter {
    /**
     * cursor的起点
     */
    private long cursorFrom = 0;
    /**
     * cursor的终点
     */
    private long cursorTo = Long.MAX_VALUE;

    /**
     * topic
     */
    private String topic;

    private String consumerId;

    /**
     * 拉取消息的一批大小
     */
    private int batchSize = 100;
}
