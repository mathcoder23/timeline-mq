package org.pettyfox.timeline.core;


import org.junit.Test;
import org.pettyfox.timeline.model.Timeline;
import org.pettyfox.timeline.store.TimelineStoreTestImpl;
import org.pettyfox.timeline.strategy.status.TimelineMessageStatusFanout;

/**
 * Timeline 服务，提供对外接口
 * @author Petty Fox
 * @version 1.0
 */
public class TimelineMqTest {
    @Test
    public void simple(){
        TimelineExchange exchange = TimelineExchangeFactory.createExchangeMemory();
        exchange.subscribe("userA","userB","userC");
        TimelineMq<TimelineMessageStatusFanout> mq = TimelineMqFactory.createMqStatusFanout(new TimelineStoreTestImpl(),exchange);
        sendMessage(mq,"userB","123");
        sendMessage(mq,"userB","666");
        sendMessage(mq,"userC","444");
    }
    private void sendMessage(TimelineMq<TimelineMessageStatusFanout> mq,String producerId,String messageId){
        Timeline<TimelineMessageStatusFanout> timeline = Timeline.<TimelineMessageStatusFanout>builder()
                .producerId(producerId)
                .message(TimelineMessageStatusFanout.builder()
                        .enable(true)
                        .uniqueId(messageId)
                        .build())
                .build();
        mq.push(timeline);
    }

}
