package com.pettyfox.timeline.store.impl;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/8
 */
public class RedisKeyFactory {
    private static final String REDIS_GROUP = "timeline2::";
    private static final String REDIS_PREFIX_TIMELINE = REDIS_GROUP + "TIMELINE_";
    private static final String REDIS_PREFIX_TIMELINE_OBJ = REDIS_GROUP + "TIMELINE_OBJ_";
    private static final String T_EXCHANGE_CONSUMER = REDIS_GROUP + "T_EXCHANGE_CONSUMER_";
    private static final String T_EXCHANGE_PRODUCER = REDIS_GROUP + "T_EXCHANGE_PRODUCER_";
    /**
     * 可能并没有什么用的，key缓存，防止重复计算key
     */
    private static final Cache<String, String> STRING_STRING_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .maximumSize(1000)
            .build();
    private static final Cache<String, String> STREAM_OBJ_KEY_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .maximumSize(1000)
            .build();

    /**
     * 消息队列Stream key
     *
     * @param producerId 生成者id
     * @return key
     */
    public static String timelineStreamKey(String producerId) {
        if (STRING_STRING_CACHE.getIfPresent(producerId) != null) {
            return STRING_STRING_CACHE.getIfPresent(producerId);
        }
        STRING_STRING_CACHE.put(producerId, REDIS_PREFIX_TIMELINE + producerId);
        return STRING_STRING_CACHE.getIfPresent(producerId);
    }

    /**
     * 保证timeline Stream中obj id唯一的key
     *
     * @param producerId 生成者id
     * @return key
     */
    public static String timelineObjKey(String producerId) {
        if (STREAM_OBJ_KEY_CACHE.getIfPresent(producerId) != null) {
            return STREAM_OBJ_KEY_CACHE.getIfPresent(producerId);
        }
        STREAM_OBJ_KEY_CACHE.put(producerId, REDIS_PREFIX_TIMELINE_OBJ + producerId);
        return STREAM_OBJ_KEY_CACHE.getIfPresent(producerId);
    }

    public static String exchangeConsumerKey(String consumerId) {
        return T_EXCHANGE_CONSUMER + consumerId;
    }
    public static String exchangeProducerKey(String producerId) {
        return T_EXCHANGE_PRODUCER + producerId;
    }
}
