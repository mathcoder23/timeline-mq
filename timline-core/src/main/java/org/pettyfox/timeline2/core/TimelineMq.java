package org.pettyfox.timeline2.core;


import org.pettyfox.timeline2.model.Timeline;
import org.pettyfox.timeline2.model.TimelineMessage;
import org.pettyfox.timeline2.model.TimelinePullParameter;

import java.util.List;

/**
 * Timeline 服务，提供对外接口
 * @author Petty Fox
 * @version 1.0
 */
public interface TimelineMq<T extends TimelineMessage> {
    void push(Timeline<T> timeline);
    List<Timeline<T>> pull(TimelinePullParameter parameter);
}
