package org.pettyfox.timeline.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Petty Fox
 * @version 1.0
 */
@Setter
@Getter
public class TimelinePullParameter {
    private long from = 0;
    private long to = Long.MAX_VALUE;
    private String consumerId;
    private int batchSize = 100;
}
