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
public class TimelineHead implements Serializable {
    /**
     * 严格递增id
     */
    private String id;

    /**
     * topic主题
     */
    private String topic;


    private String channel;

    /**
     * 对象id
     */
    private String objId;

    /**
     * 优化标记
     */
    private Boolean optimizeFlag;
}
