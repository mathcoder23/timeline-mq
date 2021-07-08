package org.pettyfox.timeline2.store.impl;


import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.pettyfox.timeline2.model.TimelineMessage;
import org.pettyfox.timeline2.model.TimelinePullParameter;
import org.pettyfox.timeline2.store.TimelineMqStore;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 基于内存的消息存储
 *
 * @author Petty Fox
 * @version 1.0
 */
@Service
@Slf4j
public class TimelineMqStoreRedisImpl implements TimelineMqStore {
    private ConcurrentHashMap<String, List<TimelineMessage>> store = new ConcurrentHashMap<>();
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void store(TimelineMessage timelineMessage) {
        List<TimelineMessage> list = store.computeIfAbsent(timelineMessage.getTopic(), (key) -> new CopyOnWriteArrayList<>());
        list.add(timelineMessage);
        redisTemplate.opsForStream().add(Record.of(BeanUtil.beanToMap(timelineMessage)).withStreamKey(RedisKeyFactory.timelineStreamKey(timelineMessage.getTopic())));
    }

    @Override
    public List<TimelineMessage> listTimeline(TimelinePullParameter... parameter) {
        return listTimeline(Arrays.asList(parameter));
    }

    @Override
    public List<TimelineMessage> listTimeline(List<TimelinePullParameter> parameter) {
        List<TimelineMessage> result = new ArrayList<>();
        for (TimelinePullParameter p : parameter) {

            StreamOffset<String> offset = StreamOffset.create(RedisKeyFactory.timelineStreamKey(p.getTopic()), ReadOffset.lastConsumed());
            Consumer consumer = Consumer.from(p.getConsumerId(), "t");
            try {
                List<MapRecord<String, Object, Object>> list = redisTemplate.opsForStream().read(consumer, StreamReadOptions.empty().count(p.getBatchSize()), offset);
                result.addAll(list.stream().map(m -> {
                    TimelineMessage message = BeanUtil.mapToBean(m.getValue(), TimelineMessage.class, false, null);
                    message.setId(m.getId().getValue());
                    return message;
                }).collect(Collectors.toList()));
            } catch (Exception e) {
                Optional.ofNullable(redisTemplate.hasKey(RedisKeyFactory.timelineStreamKey(p.getTopic()))).ifPresent(b -> {
                    if (b) {
                        log.error("list timeline error:", e);
                    }
                });
            }

        }
        return result;
    }
}
