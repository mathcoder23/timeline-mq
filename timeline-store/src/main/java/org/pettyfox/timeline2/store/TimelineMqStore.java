package org.pettyfox.timeline2.store;


import org.pettyfox.timeline2.model.TimelineMessage;
import org.pettyfox.timeline2.model.TimelinePullParameter;

import java.util.List;

/**
 * @author Petty Fox
 * @version 1.0
 */
public interface TimelineMqStore {

    void store(TimelineMessage timelineMessage);

    List<TimelineMessage> listTimeline(TimelinePullParameter... parameter);
    List<TimelineMessage> listTimeline(List<TimelinePullParameter> parameter);
}
