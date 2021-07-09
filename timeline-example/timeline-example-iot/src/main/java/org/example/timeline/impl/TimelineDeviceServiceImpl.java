package org.example.timeline.impl;

import cn.hutool.core.lang.Snowflake;
import com.alibaba.fastjson.JSON;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.DeviceDto;
import org.example.dto.GuardData;
import org.example.timeline.StoreService;
import org.example.timeline.TimelineDeviceService;
import org.example.timeline.event.GuardDataEvent;
import org.example.timeline.event.GuardGroupDeviceEvent;
import org.example.timeline.event.GuardGroupEvent;
import org.pettyfox.timeline2.core.TimelineCursorMq;
import org.pettyfox.timeline2.core.TimelineMqConsumerListener;
import org.pettyfox.timeline2.core.TimelineMqConsumerTimeout;
import org.pettyfox.timeline2.core.TimelineMqFactory;
import org.pettyfox.timeline2.model.TimelineHead;
import org.pettyfox.timeline2.model.TimelineMessage;
import org.pettyfox.timeline2.store.TimelineConsumerCursorStore;
import org.pettyfox.timeline2.store.TimelineExchangeStore;
import org.pettyfox.timeline2.store.TimelineMqStore;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/1
 */
@Component
@Slf4j
public class TimelineDeviceServiceImpl implements TimelineDeviceService,
        TimelineMqConsumerListener,
        GuardDataEvent,
        GuardGroupDeviceEvent,
        GuardGroupEvent,
        TimelineMqConsumerTimeout {
    private final Snowflake snowflake = new Snowflake(1, 1);
    @Resource
    private TimelineExchangeStore exchange;
    @Resource
    private TimelineConsumerCursorStore timelineConsumerCursorStore;

    @Resource
    private TimelineMqStore timelineMqStore;

    private TimelineCursorMq timelineMq;

    @Resource
    private StoreService storeService;
    @Resource
    private MetricRegistry metricRegistry;

    @PostConstruct
    public void init() {
        timelineMq = TimelineMqFactory.createCursorMq(timelineMqStore, exchange, timelineConsumerCursorStore);
        timelineMq.setTimeoutListener(this);
    }

    @Override
    public void deviceOnline(DeviceDto deviceDto) {
        log.info("device online:{}", deviceDto.getSn());
        timelineMq.registerConsumer(deviceDto.getSn(), 100, this);

    }

    @Override
    public void deviceOffline(DeviceDto deviceDto) {
        log.info("device offline:{}", deviceDto.getSn());
        timelineMq.unregisterConsumer(deviceDto.getSn());
    }

    @Override
    public boolean timeout(String consumerId, TimelineHead timelineHead) {
        log.info("timeout {},{}", consumerId, timelineHead.getId());
        return false;
    }

    @Override
    public void batchConsumer(List<TimelineMessage> queue, String consumerId) {

        Meter meter = metricRegistry.meter("consumer_" + consumerId);
        for (TimelineMessage message : queue) {
            log.info("consumer:{},queue:{}", consumerId, message.getBody());
            timelineMq.consumerAck(consumerId, message);
            meter.mark();
        }


//        log.info("batch consumer:{}, queue size:{}", consumerId, queue.size());
    }

    //门禁数据消息管理
    @Override
    public void onDataAddGroup(Long guardDataId, Long groupId) {
        TimelineMessage timelineMessage = new TimelineMessage();
        timelineMessage.setTopic(String.valueOf(groupId));
        timelineMessage.setBody(JSON.toJSONString(storeService.getGuardDataMap().get(guardDataId)));
        timelineMessage.setObjId(String.valueOf(guardDataId));
        timelineMessage.setOptimizeFlag(false);
        timelineMq.push(timelineMessage);
    }

    @Override
    public void onDataDelGroup(Long guardDataId, Long groupId) {
        TimelineMessage timelineMessage = new TimelineMessage();
        timelineMessage.setTopic(String.valueOf(groupId));
        timelineMessage.setBody("remove");
        timelineMessage.setOptimizeFlag(false);
        timelineMessage.setObjId(String.valueOf(guardDataId));
        timelineMq.push(timelineMessage);
    }

    @Override
    public void onDataDel(Long guardDataId) {
        GuardData guardData = storeService.getGuardDataMap().get(guardDataId);
        if (null == guardData) {
            return;
        }
        TimelineMessage timelineMessage = new TimelineMessage();
        timelineMessage.setTopic(String.valueOf(guardData.getGuardGroupId()));
        timelineMessage.setBody("remove");
        timelineMessage.setOptimizeFlag(false);
        timelineMessage.setObjId(String.valueOf(guardDataId));
        timelineMq.push(timelineMessage);
    }

    @Override
    public void onDataModify(Long guardDataId) {
        GuardData guardData = storeService.getGuardDataMap().get(guardDataId);
        if (null == guardData) {
            return;
        }
        TimelineMessage timelineMessage = new TimelineMessage();
        timelineMessage.setTopic(String.valueOf(guardData.getGuardGroupId()));
        timelineMessage.setBody(JSON.toJSONString(storeService.getGuardDataMap().get(guardDataId)));
        timelineMessage.setOptimizeFlag(false);
        timelineMessage.setObjId(String.valueOf(guardDataId));
        timelineMq.push(timelineMessage);
    }

    @Override
    public void onDelGroup(String groupId) {
        exchange.removeAllSubscribeByBeSubscribe(groupId);
    }

    //设备、门禁组管理
    @Override
    public void onAddGroup(String deviceSn, Long groupId) {
        exchange.subscribe(deviceSn, String.valueOf(groupId));
        timelineMq.wakeupConsumer(deviceSn);
    }

    @Override
    public void onDelGroup(String deviceSn, Long groupId) {
        exchange.unsubscribe(deviceSn, String.valueOf(groupId));
        timelineConsumerCursorStore.removeConsumer(deviceSn);
    }
}
