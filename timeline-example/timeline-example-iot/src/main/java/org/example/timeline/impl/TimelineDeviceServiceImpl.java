package org.example.timeline.impl;

import cn.hutool.core.lang.Snowflake;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.DeviceDto;
import org.example.dto.GuardData;
import org.example.timeline.StoreService;
import org.example.timeline.TimelineDeviceService;
import org.example.timeline.event.GuardDataEvent;
import org.example.timeline.event.GuardGroupDeviceEvent;
import org.pettyfox.timeline2.core.TimelineCursorMq;
import org.pettyfox.timeline2.core.TimelineMqConsumerListener;
import org.pettyfox.timeline2.core.TimelineMqConsumerTimeout;
import org.pettyfox.timeline2.core.TimelineMqFactory;
import org.pettyfox.timeline2.model.TimelineHead;
import org.pettyfox.timeline2.model.TimelineMessage;
import org.pettyfox.timeline2.store.TimelineConsumerCursorStore;
import org.pettyfox.timeline2.store.TimelineExchange;
import org.pettyfox.timeline2.store.impl.TimelineConsumerCursorStoreMemoryImpl;
import org.pettyfox.timeline2.store.impl.TimelineExchangeMemoryImpl;
import org.pettyfox.timeline2.store.impl.TimelineMqStoreMemoryImpl;
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
public class TimelineDeviceServiceImpl implements TimelineDeviceService, TimelineMqConsumerListener, GuardDataEvent, GuardGroupDeviceEvent, TimelineMqConsumerTimeout {
    private final Snowflake snowflake = new Snowflake(1, 1);
    private TimelineExchange exchange = new TimelineExchangeMemoryImpl();
    private TimelineConsumerCursorStore timelineConsumerCursorStore = new TimelineConsumerCursorStoreMemoryImpl();
    private TimelineCursorMq timelineMq = TimelineMqFactory.createCursorMq(new TimelineMqStoreMemoryImpl(), exchange, timelineConsumerCursorStore);

    @Resource
    private StoreService storeService;

    @PostConstruct
    public void init() {
        timelineMq.setTimeoutListener(this);
    }

    @Override
    public void deviceOnline(DeviceDto deviceDto) {
        log.info("device online:{}", deviceDto.getSn());
        timelineMq.registerConsumer(deviceDto.getSn(), 10, this);
    }

    @Override
    public void deviceOffline(DeviceDto deviceDto) {
        log.info("device offline:{}", deviceDto.getSn());
        timelineMq.unregisterConsumer(deviceDto.getSn());
    }

    @Override
    public void timeout(String consumerId, TimelineHead timelineHead) {
        log.info("timeout {},{}", consumerId, timelineHead.getId());
    }

    @Override
    public void batchConsumer(List<TimelineMessage> queue, String consumerId) {
        for (TimelineMessage message : queue) {
            log.info("consumer:{},queue:{}", consumerId, message.getBody());
        }
        log.info("batch consumer:{}, queue size:{}", consumerId, queue.size());
        timelineMq.consumerAck(consumerId, queue.get(queue.size() - 1));
    }

    //门禁数据消息管理
    @Override
    public void onDataAddGroup(Long guardDataId, Long groupId) {
        TimelineMessage timelineMessage = new TimelineMessage();
        timelineMessage.setTopic(String.valueOf(groupId));
        timelineMessage.setBody(JSON.toJSONString(storeService.getGuardDataMap().get(guardDataId)));
        timelineMessage.setId(snowflake.nextId());
        timelineMessage.setObjId(String.valueOf(guardDataId));
        timelineMq.push(timelineMessage);
    }

    @Override
    public void onDataDelGroup(Long guardDataId, Long groupId) {
        TimelineMessage timelineMessage = new TimelineMessage();
        timelineMessage.setTopic(String.valueOf(groupId));
        timelineMessage.setBody("remove");
        timelineMessage.setId(snowflake.nextId());
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
        timelineMessage.setId(snowflake.nextId());
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
        timelineMessage.setId(snowflake.nextId());
        timelineMessage.setObjId(String.valueOf(guardDataId));
        timelineMq.push(timelineMessage);
    }

    //设备、门禁组管理
    @Override
    public void onAddGroup(String deviceSn, Long groupId) {
        exchange.subscribe(deviceSn, String.valueOf(groupId));
    }

    @Override
    public void onDelGroup(String deviceSn, Long groupId) {
        exchange.unsubscribe(deviceSn, String.valueOf(groupId));

    }
}
