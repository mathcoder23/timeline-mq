package org.pettyfox.timeline.core;


import org.pettyfox.timeline.model.Timeline;
import org.pettyfox.timeline.model.TimelineMessage;
import org.pettyfox.timeline.model.TimelinePullParameter;

import java.util.List;

/**
 * Timeline 服务，提供对外接口
 * @author Petty Fox
 * @version 1.0
 */
public interface TimelineMq<T extends TimelineMessage> {
    void push(Timeline<? extends T> timeline);
    List<Timeline<? extends T>>  pull(TimelinePullParameter parameter);
}
