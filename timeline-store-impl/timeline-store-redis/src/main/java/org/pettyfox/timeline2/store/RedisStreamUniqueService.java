package org.pettyfox.timeline2.store;

import org.pettyfox.timeline2.store.impl.RedisKeyFactory;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 确保Redis Stream中队列的objId唯一
 *
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/9
 */
@Component
public class RedisStreamUniqueService {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public void addRecord(String producerId, String objId, RecordId recordId) {
        String key = RedisKeyFactory.timelineObjKey(producerId);
        //单机版加锁
        String lock = producerId + objId;
        synchronized (lock.intern()) {
            if (Objects.equals(redisTemplate.opsForHash().hasKey(key, objId), true)) {
                String id = (String) redisTemplate.opsForHash().get(key, objId);
                String steamKey = RedisKeyFactory.timelineStreamKey(producerId);
                redisTemplate.opsForStream().delete(steamKey, id);
            }
            redisTemplate.opsForHash().put(key, objId, recordId.getValue());
        }
    }

    public void deleteProducer(String producerId) {
        String key = RedisKeyFactory.timelineObjKey(producerId);
        redisTemplate.delete(key);
        redisTemplate.delete(RedisKeyFactory.timelineStreamKey(producerId));
    }
}
