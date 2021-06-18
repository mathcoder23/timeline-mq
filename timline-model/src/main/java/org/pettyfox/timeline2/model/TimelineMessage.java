package org.pettyfox.timeline2.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

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
