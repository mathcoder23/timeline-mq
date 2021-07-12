package com.pettyfox.timeline.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/6/17 11:34
 */
@Getter
@Setter
public class TimelineConsumerFailed {

    private Long id;
    /**
     * 消费者id
     */
    private String consumerId;
    /**
     * 消息id
     */
    private Long messageId;
    /**
     * 失败次数
     */
    private int failedCount;
    /**
     * 消息对象id
     */
    private String messageObjId;
    /**
     * 失败时间
     */
    private Date failedDate;
}
