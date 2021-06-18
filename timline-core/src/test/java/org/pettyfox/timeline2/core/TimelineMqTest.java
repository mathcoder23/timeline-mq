package org.pettyfox.timeline2.core;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.pettyfox.timeline2.model.Timeline;
import org.pettyfox.timeline2.model.TimelinePullParameter;
import org.pettyfox.timeline2.store.TimelineStoreMemoryImpl;
import org.pettyfox.timeline2.strategy.status.TimelineMessageStatusFanout;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Petty Fox
 * @version 1.0
 */
@Slf4j
public class TimelineMqTest {
    @Test
    public void simple() throws InterruptedException {
        TimelineExchange exchange = TimelineExchangeFactory.createExchangeMemory();
        exchange.subscribe("userA","userB");
        TimelineMq<TimelineMessageStatusFanout> mq = TimelineMqFactory.createMqStatusFanout(new TimelineStoreMemoryImpl<TimelineMessageStatusFanout>(),exchange);
        sendMessage(mq,"userB","123",1L);
        sendMessage(mq,"userB","666",2L);
        sendMessage(mq,"userC","444",3L);
        new Thread(()->{
            TimelinePullParameter pullParameter = new TimelinePullParameter();
            pullParameter.setBatchSize(10);
            pullParameter.setConsumerId("userA");
            List<Timeline<TimelineMessageStatusFanout>> list = mq.pull(pullParameter);
            list.forEach(en->{
                log.info("item:{},m:{}",en.getProducerId(),en.getMessage().getUniqueId());
            });
        }).start();
        new CountDownLatch(1).await();
    }
    private void sendMessage(TimelineMq<TimelineMessageStatusFanout> mq,String producerId,String messageId,long sId){
        Timeline<TimelineMessageStatusFanout> timeline = Timeline.<TimelineMessageStatusFanout>builder()
                .producerId(producerId)
                .sequenceId(sId)
                .message(TimelineMessageStatusFanout.builder()
                        .enable(true)
                        .uniqueId(messageId)
                        .build())
                .build();
        mq.push(timeline);
    }

    @Test
    public void multiData() throws InterruptedException {
        TimelineExchange exchange = TimelineExchangeFactory.createExchangeMemory();
        exchange.subscribe("userA","userB");
        exchange.subscribe("userB","userC");
        TimelineMq<TimelineMessageStatusFanout> mq = TimelineMqFactory.createMqStatusFanout(new TimelineStoreMemoryImpl<TimelineMessageStatusFanout>(),exchange);

        for(long i = 0;i<100000;){
            sendMessage(mq,"userB","123"+i,i);
            i+=2;
        }
        for(long i = 1;i<100000;){
            sendMessage(mq,"userC","cc"+i,i);
            i+=2;
        }
        new Thread(()->{
            TimelinePullParameter pullParameter = new TimelinePullParameter();
            pullParameter.setFrom(0L);
            pullParameter.setBatchSize(5);
            while(true){
                pullParameter.setConsumerId("userA");
                List<Timeline<TimelineMessageStatusFanout>> list = mq.pull(pullParameter);
                list.forEach(en->{
                    log.info("item:{},m:{}",en.getProducerId(),en.getSequenceId());
                    pullParameter.setFrom(en.getSequenceId()+1);
                });
                try {
                    Thread.sleep(1000);
                    log.info("next batch");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        new Thread(()->{
            TimelinePullParameter pullParameter = new TimelinePullParameter();
            pullParameter.setFrom(0L);
            pullParameter.setBatchSize(5);
            while(true){
                pullParameter.setConsumerId("userB");
                List<Timeline<TimelineMessageStatusFanout>> list = mq.pull(pullParameter);
                list.forEach(en->{
                    log.info("item:{},m:{}",en.getProducerId(),en.getSequenceId());
                    pullParameter.setFrom(en.getSequenceId()+1);
                });
                try {
                    Thread.sleep(1000);
                    log.info("next batch2");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new CountDownLatch(1).await();
    }
}
