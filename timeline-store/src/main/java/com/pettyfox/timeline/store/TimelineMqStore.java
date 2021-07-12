package com.pettyfox.timeline.store;



import com.pettyfox.timeline.model.TimelineMessage;
import com.pettyfox.timeline.model.TimelinePullParameter;

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
