package com.pettyfox.timeline.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Petty Fox
 * @version 1.0
 */
@Getter
@Setter
public class TimelineMessage extends TimelineHead {

    /**
     * 消息体
     */
    private String body;
}
