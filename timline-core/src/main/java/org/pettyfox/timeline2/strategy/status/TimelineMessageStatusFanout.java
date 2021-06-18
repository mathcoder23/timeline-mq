package org.pettyfox.timeline2.strategy.status;

import lombok.*;
import org.pettyfox.timeline2.model.TimelineMessage;

/**
 * @author Petty Fox
 * @version 1.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineMessageStatusFanout extends TimelineMessage {
    private Boolean enable;
    private String uniqueId;
}
