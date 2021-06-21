package org.pettyfox.timeline2.strategy;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import lombok.extern.slf4j.Slf4j;
import org.pettyfox.timeline2.core.*;
import org.pettyfox.timeline2.model.TimelineHead;
import org.pettyfox.timeline2.model.TimelineMessage;
import org.pettyfox.timeline2.model.TimelinePullParameter;
import org.pettyfox.timeline2.store.TimelineConsumerCursorStore;
import org.pettyfox.timeline2.store.TimelineExchange;
import org.pettyfox.timeline2.store.TimelineMqStore;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Timeline 服务，提供对外接口
 *
 * @author Petty Fox
 * @version 1.0
 */
@Slf4j
public class TimelineMqCursorImpl implements TimelineCursorMq {
    private final TimelineMqStore timelineStore;
    private final TimelineExchange timelineExchange;
    private final TimelineConsumerCursorStore timelineConsumerCursorStore;
    private final TimelineMqConsumerPool consumerPool = new TimelineMqConsumerPool(this);
    private final ConcurrentHashMap<String, Cache<String, TimelineHead>> consumerPendingCache = new ConcurrentHashMap<>();
    private TimelineMqConsumerTimeout timelineMqConsumerTimeout;

    public TimelineMqCursorImpl(TimelineMqStore timelineStore, TimelineExchange timelineExchange, TimelineConsumerCursorStore timelineConsumerCursorStore) {
        this.timelineStore = timelineStore;
        this.timelineExchange = timelineExchange;
        this.timelineConsumerCursorStore = timelineConsumerCursorStore;
    }


    @Override
    public void push(TimelineMessage timelineMessage) {
        timelineStore.store(timelineMessage);
    }

    @Override
    public void consumerAck(String consumerId, TimelineHead timelineHead) {
        timelineConsumerCursorStore.storeConsumerAck(consumerId, timelineHead);
    }

    @Override
    public List<TimelineMessage> pull(String consumerId, int batchSize) {
        if (hasPending(consumerId)) {
            return null;
        }
        List<String> producerIds = timelineExchange.listByBeSubscribe(consumerId);
        if (null == producerIds || producerIds.isEmpty()) {
            return null;
        }
        List<TimelinePullParameter> parameter = timelineConsumerCursorStore.listConsumerCursor(consumerId, producerIds);
        parameter.forEach(t -> t.setBatchSize(batchSize));
        List<TimelineMessage> list = timelineStore.listTimeline(parameter);
        addPending(consumerId, list);
        return list;
    }

    @Override
    public void registerConsumer(String consumerId, int batchSize, TimelineMqConsumerListener listener) {
        ConsumerSession consumerSession = new ConsumerSession();
        consumerSession.setConsumerId(consumerId);
        consumerSession.setBatchSize(batchSize);
        consumerSession.setListener(listener);
        consumerPool.addConsumer(consumerSession);
    }

    @Override
    public void unregisterConsumer(String consumerId) {
        consumerPool.removeConsumer(consumerId);
    }

    @Override
    public void setTimeoutListener(TimelineMqConsumerTimeout timeoutListener) {
        this.timelineMqConsumerTimeout = timeoutListener;
    }

    private boolean hasPending(String consumerId) {
        return consumerPendingCache.containsKey(consumerId) && consumerPendingCache.get(consumerId).size() != 0;
    }

    private void addPending(String consumerId, List<TimelineMessage> list) {
        Cache<String, TimelineHead> cache = consumerPendingCache.computeIfAbsent(consumerId, key -> CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .initialCapacity(100)
                .maximumSize(1000)
                //重要，这个值必须为1，否则影响扩容的准确性，导致以size的类型被驱逐
                .concurrencyLevel(1)
                /**
                 * 同步列队超时或异常监听
                 */
                .removalListener(removalNotification -> {
                    //超时同步队列
                    if (removalNotification.getCause().equals(RemovalCause.EXPIRED)) {
                        timelineMqConsumerTimeout.timeout(consumerId,(TimelineHead) removalNotification.getValue());
                    } else if (!removalNotification.getCause().equals(RemovalCause.EXPLICIT)) {
                        log.debug("sync ack syncId:{},remove type:{}", removalNotification.getKey(), removalNotification.getCause());
                    }
                }).build());
        list.forEach(k -> cache.put(String.valueOf(k.getId()), k));
    }
}
