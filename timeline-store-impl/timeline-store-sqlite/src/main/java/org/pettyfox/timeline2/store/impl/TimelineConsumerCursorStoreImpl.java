package org.pettyfox.timeline2.store.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.pettyfox.timeline2.model.TimelineHead;
import org.pettyfox.timeline2.model.TimelinePullParameter;
import org.pettyfox.timeline2.store.TimelineConsumerCursorStore;
import org.pettyfox.timeline2.store.models.biz.TimelineConsumerCursorBiz;
import org.pettyfox.timeline2.store.models.entity.TimelineConsumerCursorRecord;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/6/17 13:55
 */
@Slf4j
@Component
public class TimelineConsumerCursorStoreImpl implements TimelineConsumerCursorStore {
    @Resource
    private TimelineConsumerCursorBiz timelineConsumerCursorBiz;

    @Override
    public void storeConsumerAck(String consumerId, TimelineHead timelineHead) {
        if (timelineConsumerCursorBiz.hasConsumer(consumerId)) {
            LambdaUpdateWrapper<TimelineConsumerCursorRecord> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(TimelineConsumerCursorRecord::getConsumerId, consumerId);
            updateWrapper.eq(TimelineConsumerCursorRecord::getProducerId, timelineHead.getTopic());
            updateWrapper.lt(TimelineConsumerCursorRecord::getCursorId, timelineHead.getId());
            updateWrapper.set(TimelineConsumerCursorRecord::getCursorId, timelineHead.getId());
            timelineConsumerCursorBiz.update(updateWrapper);
        } else {
            TimelineConsumerCursorRecord timelineConsumerCursor = new TimelineConsumerCursorRecord();
            timelineConsumerCursor.setConsumerId(consumerId);
            timelineConsumerCursor.setProducerId(timelineHead.getTopic());
            timelineConsumerCursor.setCursorId(timelineHead.getId());
            timelineConsumerCursorBiz.save(timelineConsumerCursor);
        }

    }

    @Override
    public List<TimelinePullParameter> listConsumerCursor(String consumerId, List<String> producerIds) {
        return producerIds.stream().map(p -> {
            TimelinePullParameter parameter = new TimelinePullParameter();
            parameter.setTopic(p);
            TimelineConsumerCursorRecord record = timelineConsumerCursorBiz.getCursor(consumerId, p);
            if (null != record) {
                parameter.setCursorFrom(Optional.ofNullable(record.getCursorId()).orElse(0L));
            }
            return parameter;
        }).collect(Collectors.toList());
    }

    @Override
    public void removeConsumer(String consumerId) {
        timelineConsumerCursorBiz.removeConsumer(consumerId);
    }
}
