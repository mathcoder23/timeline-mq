package org.pettyfox.timeline2.store.impl;


import org.pettyfox.timeline2.store.TimelineExchangeStore;
import org.pettyfox.timeline2.store.models.biz.TimelineExchangeBiz;
import org.pettyfox.timeline2.store.models.entity.TimelineExchange;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Petty Fox
 * @version 1.0
 */
@Component
public class TimelineExchangeStoreImpl implements TimelineExchangeStore {
    @Resource
    private TimelineExchangeBiz timelineExchangeBiz;

    @Override
    public void subscribe(String consumerId, String... producerIds) {
        for (String producerId : producerIds) {
            if (!timelineExchangeBiz.hasConsumerBindProducer(consumerId, producerId)) {
                TimelineExchange timelineExchange = new TimelineExchange();
                timelineExchange.setConsumerId(consumerId);
                timelineExchange.setProducerId(producerId);
                timelineExchangeBiz.save(timelineExchange);
            }
        }
    }

    @Override
    public void subscribe(String consumerId, Set<String> producerIds) {
        for (String producerId : producerIds) {
            if (!timelineExchangeBiz.hasConsumerBindProducer(consumerId, producerId)) {
                TimelineExchange timelineExchange = new TimelineExchange();
                timelineExchange.setConsumerId(consumerId);
                timelineExchange.setProducerId(producerId);
                timelineExchangeBiz.save(timelineExchange);
            }
        }
    }

    @Override
    public void unsubscribe(String consumerId, String producerId) {
        timelineExchangeBiz.unbind(consumerId, producerId);
    }

    @Override
    public void removeAllSubscribe(String consumerId) {
        timelineExchangeBiz.unbindConsumerId(consumerId);
    }

    @Override
    public void removeAllSubscribeByBeSubscribe(String producerId) {
        timelineExchangeBiz.unbindProducerId(producerId);
    }

    @Override
    public List<String> listBySubscribe(String producerId) {
        return timelineExchangeBiz.listByProducer(producerId).stream()
                .map(TimelineExchange::getConsumerId).collect(Collectors.toList());
    }

    @Override
    public List<String> listByBeSubscribe(String consumerId) {
        return timelineExchangeBiz.listByConsumer(consumerId)
                .stream().map(TimelineExchange::getProducerId).collect(Collectors.toList());
    }
}
