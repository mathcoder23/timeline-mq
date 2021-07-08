package org.pettyfox.timeline2.store.impl;


import org.pettyfox.timeline2.store.TimelineExchangeStore;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Petty Fox
 * @version 1.0
 */
@Component
public class TimelineExchangeRedisImpl implements TimelineExchangeStore {
    private static final String T_EXCHANGE_CONSUMER = "T_EXCHANGE_CONSUMER_";
    private static final String T_EXCHANGE_PRODUCER = "T_EXCHANGE_PRODUCER_";
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private String getConsumerKey(String consumerId) {
        return T_EXCHANGE_CONSUMER + consumerId;
    }

    private String getProducerKey(String producerId) {
        return T_EXCHANGE_PRODUCER + producerId;
    }

    @Override
    public void subscribe(String consumerId, String... producerIds) {
        for (String producerId : producerIds) {
            redisTemplate.opsForSet().add(getProducerKey(producerId), consumerId);
            bindRedisConsumerGroup(consumerId, producerId);
        }
        redisTemplate.opsForSet().add(getConsumerKey(consumerId), producerIds);


    }

    @Override
    public void subscribe(String consumerId, Set<String> producerIds) {
        for (String producerId : producerIds) {
            redisTemplate.opsForSet().add(getProducerKey(producerId), consumerId);
            redisTemplate.opsForSet().add(getConsumerKey(consumerId), producerId);
            bindRedisConsumerGroup(consumerId, producerId);
        }
    }

    @Override
    public void unsubscribe(String consumerId, String producerId) {
        redisTemplate.opsForSet().remove(getConsumerKey(consumerId), producerId);
        redisTemplate.opsForSet().add(getProducerKey(producerId), consumerId);
        deleteRedisConsumer(consumerId, producerId);
    }

    @Override
    public void removeAllSubscribe(String consumerId) {
        Set<String> list = redisTemplate.opsForSet().members(getConsumerKey(consumerId));
        if (null != list && !list.isEmpty()) {
            list.forEach(item -> {
                redisTemplate.opsForSet().remove(getProducerKey(item), consumerId);
            });
        }
        redisTemplate.delete(getConsumerKey(consumerId));
        //TODO 解绑消费组
    }

    @Override
    public void removeAllSubscribeByBeSubscribe(String producerId) {
        Set<String> list = redisTemplate.opsForSet().members(getProducerKey(producerId));
        if (null != list && !list.isEmpty()) {
            list.forEach(item -> {
                redisTemplate.opsForSet().remove(getConsumerKey(item), producerId);
            });
        }
        redisTemplate.delete(getProducerKey(producerId));
        //TODO 解绑消费组
    }

    @Override
    public List<String> listBySubscribe(String producerId) {
        Set<String> list = redisTemplate.opsForSet().members(getProducerKey(producerId));
        if (null != list && !list.isEmpty()) {
            return new ArrayList<>(list);
        } else {
            return null;
        }
    }

    @Override
    public List<String> listByBeSubscribe(String consumerId) {
        Set<String> list = redisTemplate.opsForSet().members(getConsumerKey(consumerId));
        if (null != list && !list.isEmpty()) {
            return new ArrayList<>(list);
        } else {
            return null;
        }
    }

    @Override
    public void onConsumerOnline(String consumerId) {
        List<String> producerList = listByBeSubscribe(consumerId);
        for (String producerId : producerList) {
            bindRedisConsumerGroup(consumerId, producerId);
        }

    }

    private void bindRedisConsumerGroup(String consumerId, String producerId) {
        String streamKey = RedisKeyFactory.timelineStreamKey(producerId);
        Optional.ofNullable(redisTemplate.hasKey(streamKey)).ifPresent(b -> {
            if (b) {
                StreamInfo.XInfoGroups groups = redisTemplate.opsForStream().groups(streamKey);
                if (groups.stream().noneMatch(group -> group.groupName().equals(consumerId))) {
                    redisTemplate.opsForStream().createGroup(streamKey, consumerId);
                }
            } else {
                redisTemplate.opsForStream().createGroup(streamKey, consumerId);
            }
        });
    }

    private void deleteRedisConsumer(String consumerId, String producerId) {
        String streamKey = RedisKeyFactory.timelineStreamKey(producerId);
        Optional.ofNullable(redisTemplate.hasKey(streamKey)).ifPresent(b -> {
            if (b) {
                StreamInfo.XInfoGroups groups = redisTemplate.opsForStream().groups(streamKey);
                if (groups.stream().anyMatch(group -> group.groupName().equals(consumerId))) {
                    redisTemplate.opsForStream().destroyGroup(streamKey, consumerId);
                }
            }
        });
    }

    @Override
    public void onConsumerOffline(String consumerId) {

    }
}
