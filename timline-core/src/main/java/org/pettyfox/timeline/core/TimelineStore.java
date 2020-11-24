package org.pettyfox.timeline.core;


import org.pettyfox.timeline.model.Timeline;
import org.pettyfox.timeline.model.TimelineMessage;
import org.pettyfox.timeline.model.TimelinePullParameter;

import java.util.List;

/**
 * @author Petty Fox
 * @version 1.0
 */
public interface TimelineStore<T extends TimelineMessage> {

    void store(Timeline<T> timeline);

    List<Timeline<T>> listTimeline(List<String> producerIds, TimelinePullParameter parameter);
}
