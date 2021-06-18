package org.pettyfox.timeline2.model;

import lombok.*;


/**
 * @author Petty Fox
 * @version 1.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Timeline<T extends TimelineMessage> {
    private Long sequenceId;
    private String producerId;
    private T message;
}
