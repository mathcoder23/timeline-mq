package com.pettyfox.timeline.store.impl;


import cn.hutool.core.bean.BeanUtil;
import com.pettyfox.timeline.model.TimelineMessage;
import com.pettyfox.timeline.model.TimelinePullParameter;
import com.pettyfox.timeline.store.RedisStreamUniqueService;
import com.pettyfox.timeline.store.TimelineMqStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 基于Redis的消息存储
 *
 * @author Petty Fox
 * @version 1.0
 */
@Service
@Slf4j
public class TimelineMqStoreRedisImpl implements TimelineMqStore {
    private ConcurrentHashMap<String, List<TimelineMessage>> store = new ConcurrentHashMap<>();
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private RedisStreamUniqueService redisStreamUniqueService;

    @Override
    public void store(TimelineMessage timelineMessage) {
        List<TimelineMessage> list = store.computeIfAbsent(timelineMessage.getTopic(), (key) -> new CopyOnWriteArrayList<>());
        list.add(timelineMessage);
        RecordId recordId = redisTemplate.opsForStream().add(Record.of(BeanUtil.beanToMap(timelineMessage)).withStreamKey(RedisKeyFactory.timelineStreamKey(timelineMessage.getTopic())));
        if (null != timelineMessage.getObjId()) {
            redisStreamUniqueService.addRecord(timelineMessage.getTopic(), timelineMessage.getObjId(), recordId);
        }
    }

    @Override
    public List<TimelineMessage> listTimeline(TimelinePullParameter... parameter) {
        return listTimeline(Arrays.asList(parameter));
    }

    @Override
    public List<TimelineMessage> listTimeline(List<TimelinePullParameter> parameter) {
        List<TimelineMessage> result = new ArrayList<>();
        for (TimelinePullParameter p : parameter) {
            if (result.size() >= p.getBatchSize()) {
                break;
            }
            StreamOffset<String> offset = StreamOffset.create(RedisKeyFactory.timelineStreamKey(p.getTopic()), ReadOffset.lastConsumed());
            Consumer consumer = Consumer.from(p.getConsumerId(), "t1");
            try {
                Optional.ofNullable(readPending(p, consumer)).ifPresent(list -> {
                    if (!list.isEmpty()) {
                        result.addAll(list.stream().map(this::convert).collect(Collectors.toList()));
                    }
                });
                Optional.ofNullable(redisTemplate.opsForStream().read(consumer, StreamReadOptions.empty().count(p.getBatchSize()), offset))
                        .ifPresent(list -> {
                            if (!list.isEmpty()) {
                                result.addAll(list.stream().map(this::convert).collect(Collectors.toList()));
                            }
                        });
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

    private List<MapRecord<String, Object, Object>> readPending(TimelinePullParameter p, Consumer consumer) {
        PendingMessages pendingMessages = redisTemplate.opsForStream().pending(RedisKeyFactory.timelineStreamKey(p.getTopic()), consumer, Range.unbounded(), p.getBatchSize());
        if (pendingMessages.isEmpty()) {
            return null;
        }
        String streamKey = RedisKeyFactory.timelineStreamKey(p.getTopic());
        return pendingMessages.stream()
                .map(pendingMessage -> {
                    List<MapRecord<String, Object, Object>> list = redisTemplate.opsForStream()
                            .range(streamKey, Range.closed(pendingMessage.getIdAsString(), pendingMessage.getIdAsString()), RedisZSetCommands.Limit.limit().count(1));
                    if (null == list || list.isEmpty()) {
                        //清理查询不到的pending记录
                        redisTemplate.opsForStream().acknowledge(streamKey, consumer.getGroup(), pendingMessage.getIdAsString());
                    }
                    return list;
                }).filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

    }

    private TimelineMessage convert(MapRecord<String, Object, Object> m) {
        TimelineMessage message = BeanUtil.mapToBean(m.getValue(), TimelineMessage.class, false, null);
        message.setId(m.getId().getValue());
        return message;
    }
}
