package org.pettyfox.timeline2.store.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.pettyfox.timeline2.model.TimelineMessage;
import org.pettyfox.timeline2.model.TimelinePullParameter;
import org.pettyfox.timeline2.store.TimelineMqStore;
import org.pettyfox.timeline2.store.models.biz.TimelineMessageBiz;
import org.pettyfox.timeline2.store.models.entity.TimelineMessageRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于Sqlite的消息存储
 *
 * @author Petty Fox
 * @version 1.0
 */
@Component
public class TimelineMqStoreImpl implements TimelineMqStore {
    @Resource
    private TimelineMessageBiz timelineMessageBiz;

    @Override
    public void store(TimelineMessage timelineMessage) {
        TimelineMessageRecord entity = new TimelineMessageRecord();
        BeanUtils.copyProperties(timelineMessage, entity);

        //更新优化标记
        timelineMessageBiz.updateOptimizeData(entity);
        timelineMessageBiz.save(entity);
    }

    @Override
    public List<TimelineMessage> listTimeline(TimelinePullParameter... parameter) {
        return listTimeline(Arrays.asList(parameter));
    }

    @Override
    public List<TimelineMessage> listTimeline(List<TimelinePullParameter> parameter) {
        if (null == parameter || parameter.isEmpty()) {
            return null;
        }
        LambdaQueryWrapper<TimelineMessageRecord> queryWrapper = new LambdaQueryWrapper<>();

        for (TimelinePullParameter p : parameter) {
            queryWrapper.or().eq(TimelineMessageRecord::getTopic, p.getTopic())
                    //优化标记为false
                    .eq(TimelineMessageRecord::getOptimizeFlag, false)
                    .gt(TimelineMessageRecord::getId, p.getCursorFrom())
                    .le(TimelineMessageRecord::getId, p.getCursorTo());
        }
        queryWrapper.orderByAsc(TimelineMessageRecord::getId);
        queryWrapper.last("limit " + parameter.get(0).getBatchSize());

        return timelineMessageBiz.list(queryWrapper).stream().map(te -> {
            TimelineMessage timelineMessage = new TimelineMessage();
            BeanUtils.copyProperties(te, timelineMessage);
            return timelineMessage;
        }).collect(Collectors.toList());
    }
}
