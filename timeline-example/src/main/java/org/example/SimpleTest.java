package org.example;

import lombok.extern.slf4j.Slf4j;
import org.pettyfox.timeline2.core.TimelineCursorMq;
import org.pettyfox.timeline2.core.TimelineMqFactory;
import org.pettyfox.timeline2.model.TimelineMessage;
import org.pettyfox.timeline2.store.TimelineConsumerCursorStore;
import org.pettyfox.timeline2.store.TimelineExchange;
import org.pettyfox.timeline2.store.impl.TimelineConsumerCursorStoreMemoryImpl;
import org.pettyfox.timeline2.store.impl.TimelineExchangeMemoryImpl;
import org.pettyfox.timeline2.store.impl.TimelineMqStoreMemoryImpl;

import java.util.concurrent.CountDownLatch;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/6/16 16:38
 */
@Slf4j
public class SimpleTest {
    public static void main(String[] args) throws InterruptedException {
        TimelineExchange exchange = new TimelineExchangeMemoryImpl();
        TimelineConsumerCursorStore timelineConsumerCursorStore = new TimelineConsumerCursorStoreMemoryImpl();
        TimelineCursorMq timelineMq = TimelineMqFactory.createCursorMq(new TimelineMqStoreMemoryImpl(), exchange, timelineConsumerCursorStore);
        //生产消息
        for (int i = 0; i < 100; i++) {
            TimelineMessage message = new TimelineMessage();
            message.setId((long) i);
            message.setBody("测试:" + i);
            message.setTopic("123");
            timelineMq.push(message);
        }

        //订阅关系
        exchange.subscribe("c1", "123", "p1");
        timelineMq.registerConsumer("c1", 5, queue -> {
            for (TimelineMessage message : queue) {
                log.info("consumer message :{}", message.getId());
                timelineMq.consumerAck("c1", message);
            }
        });
        timelineMq.registerConsumer("c2", 5, queue -> {
            for (TimelineMessage message : queue) {
                log.info("consumer message :{}", message.getId());
                timelineMq.consumerAck("c1", message);
            }
        });
        new CountDownLatch(1).await();

    }
}