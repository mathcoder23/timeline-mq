package com.pettyfox.timeline.store.impl;

import com.pettyfox.timeline.model.TimelineHead;
import com.pettyfox.timeline.model.TimelinePullParameter;
import lombok.extern.slf4j.Slf4j;
import com.pettyfox.timeline.store.TimelineConsumerCursorStore;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/6/17 13:55
 */
@Slf4j
@Service
public class TimelineConsumerCursorStoreRedisImpl implements TimelineConsumerCursorStore {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void storeConsumerAck(String consumerId, TimelineHead timelineHead) {

        redisTemplate.opsForStream().acknowledge(RedisKeyFactory.timelineStreamKey(timelineHead.getTopic()), consumerId, timelineHead.getId());

    }

    @Override
    public List<TimelinePullParameter> listConsumerCursor(String consumerId, List<String> producerIds) {
        return null;
    }

    @Override
    public void removeConsumer(String consumerId) {
    }
}
