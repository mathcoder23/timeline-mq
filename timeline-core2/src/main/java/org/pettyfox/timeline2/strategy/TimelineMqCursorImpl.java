package org.pettyfox.timeline2.strategy;


import com.google.common.cache.*;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Timeline 服务，提供对外接口
 *
 * @author Petty Fox
 * @version 1.0
 */
@Slf4j
public class TimelineMqCursorImpl implements TimelineCursorMq, RemovalListener<String, TimelineHead> {
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
        ScheduledExecutorService timeoutTask = new ScheduledThreadPoolExecutor(1,
                new ThreadFactoryBuilder().setNameFormat("timeline-timeout-").build(), (r, executor) -> {
            log.warn("timeline-timeout exception");
        });
        timeoutTask.scheduleAtFixedRate(() -> {
            consumerPendingCache.forEach((k, c) -> {
                try {
                    c.cleanUp();
                } catch (Exception e) {
                    log.error("timeout handler exception:{}", e.getMessage());
                }
            });
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void onRemoval(RemovalNotification<String, TimelineHead> notification) {
        //超时同步队列
        if (notification.getCause().equals(RemovalCause.EXPIRED)) {
            timelineMqConsumerTimeout.timeout(notification.getKey(), notification.getValue());
        } else if (!notification.getCause().equals(RemovalCause.EXPLICIT)) {
            log.debug("sync ack syncId:{},remove type:{}", notification.getKey(), notification.getCause());
        }
    }

    @Override
    public void push(TimelineMessage timelineMessage) {
        timelineStore.store(timelineMessage);
    }

    @Override
    public void consumerAck(String consumerId, TimelineHead timelineHead) {
        timelineConsumerCursorStore.storeConsumerAck(consumerId, timelineHead);
        consumerPool.wakeupTread(consumerId);
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
                .removalListener(this).build());
        list.forEach(k -> cache.put(String.valueOf(k.getId()), k));
    }
}
