package org.pettyfox.timeline2.strategy;


import org.pettyfox.timeline2.core.ConsumerSession;
import org.pettyfox.timeline2.core.TimelineCursorMq;
import org.pettyfox.timeline2.core.TimelineMqConsumerListener;
import org.pettyfox.timeline2.core.TimelineMqConsumerPool;
import org.pettyfox.timeline2.model.TimelineHead;
import org.pettyfox.timeline2.model.TimelineMessage;
import org.pettyfox.timeline2.model.TimelinePullParameter;
import org.pettyfox.timeline2.store.TimelineConsumerCursorStore;
import org.pettyfox.timeline2.store.TimelineExchange;
import org.pettyfox.timeline2.store.TimelineMqStore;

import java.util.List;

/**
 * Timeline 服务，提供对外接口
 *
 * @author Petty Fox
 * @version 1.0
 */
public class TimelineMqCursorImpl implements TimelineCursorMq {
    private final TimelineMqStore timelineStore;
    private final TimelineExchange timelineExchange;
    private final TimelineConsumerCursorStore timelineConsumerCursorStore;
    private final TimelineMqConsumerPool consumerPool = new TimelineMqConsumerPool(this);


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
        List<String> producerIds = timelineExchange.listByBeSubscribe(consumerId);
        if (null == producerIds || producerIds.isEmpty()) {
            return null;
        }
        List<TimelinePullParameter> parameter = timelineConsumerCursorStore.listConsumerCursor(consumerId, producerIds);
        parameter.forEach(t -> t.setBatchSize(batchSize));
        return timelineStore.listTimeline(parameter);
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

}
