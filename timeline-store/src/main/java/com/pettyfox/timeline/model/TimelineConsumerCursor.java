package com.pettyfox.timeline.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/6/17 11:34
 */
@Getter
@Setter
public class TimelineConsumerCursor {
    private Long id;
    private String producerId;
    private String consumerId;
    private Long cursorId;
}
