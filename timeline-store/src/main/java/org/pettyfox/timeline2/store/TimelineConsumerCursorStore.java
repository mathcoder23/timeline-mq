package org.pettyfox.timeline2.store;

import org.pettyfox.timeline2.model.TimelineHead;
import org.pettyfox.timeline2.model.TimelinePullParameter;

import java.util.List;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/6/17 10:54
 */
public interface TimelineConsumerCursorStore {
    void storeConsumerAck(String consumerId, TimelineHead timelineHead);

    List<TimelinePullParameter> listConsumerCursor(String consumerId, List<String> producerIds);
}
