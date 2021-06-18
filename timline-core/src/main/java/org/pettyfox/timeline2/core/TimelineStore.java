package org.pettyfox.timeline2.core;


import org.pettyfox.timeline2.model.Timeline;
import org.pettyfox.timeline2.model.TimelineMessage;
import org.pettyfox.timeline2.model.TimelinePullParameter;

import java.util.List;

/**
 * @author Petty Fox
 * @version 1.0
 */
public interface TimelineStore<T extends TimelineMessage> {

    void store(Timeline<T> timeline);

    List<Timeline<T>> listTimeline(List<String> producerIds, TimelinePullParameter parameter);
}
