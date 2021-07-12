package com.pettyfox.timeline.core;


import com.pettyfox.timeline.model.TimelineMessage;
import com.pettyfox.timeline.model.TimelinePullParameter;

import java.util.List;

/**
 * Timeline 服务，提供对外接口
 * @author Petty Fox
 * @version 1.0
 */
public interface TimelineMq {
    /**
     * 推入消息
     * @param timelineMessage 消息体
     */
    void push(TimelineMessage timelineMessage);

    /**
     * 拉取消息
     * @param parameter 拉消息参数
     * @return 消息队列
     */
    List<TimelineMessage> pull(TimelinePullParameter... parameter);
}
