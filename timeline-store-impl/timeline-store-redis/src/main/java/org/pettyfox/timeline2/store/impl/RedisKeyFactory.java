package org.pettyfox.timeline2.store.impl;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/8
 */
public class RedisKeyFactory {
    private static final String REDIS_PREFIX = "TIMELINE_";

    public static String timelineStreamKey(String producerId){
        return REDIS_PREFIX + producerId;
    }
}
