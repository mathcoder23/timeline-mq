package org.example.timeline.impl;

import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.DeviceDto;
import org.example.timeline.TimelineDeviceService;
import org.pettyfox.timeline2.core.TimelineCursorMq;
import org.pettyfox.timeline2.core.TimelineMqFactory;
import org.pettyfox.timeline2.store.TimelineConsumerCursorStore;
import org.pettyfox.timeline2.store.TimelineExchange;
import org.pettyfox.timeline2.store.impl.TimelineConsumerCursorStoreMemoryImpl;
import org.pettyfox.timeline2.store.impl.TimelineExchangeMemoryImpl;
import org.pettyfox.timeline2.store.impl.TimelineMqStoreMemoryImpl;
import org.springframework.stereotype.Component;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/1
 */
@Component
@Slf4j
public class TimelineDeviceServiceImpl implements TimelineDeviceService {
    private final Snowflake snowflake = new Snowflake(1, 1);
    TimelineExchange exchange = new TimelineExchangeMemoryImpl();
    // 初始化存储引擎
    TimelineConsumerCursorStore timelineConsumerCursorStore = new TimelineConsumerCursorStoreMemoryImpl();
    // 初始化Mq
    TimelineCursorMq timelineMq = TimelineMqFactory.createCursorMq(new TimelineMqStoreMemoryImpl(), exchange, timelineConsumerCursorStore);

    @Override
    public void deviceOnline(DeviceDto deviceDto) {
        log.info("device online:{}", deviceDto.getSn());
        timelineMq.registerConsumer(deviceDto.getSn(), 10, queue -> {

        });
    }

    @Override
    public void deviceOffline(DeviceDto deviceDto) {
        log.info("device offline:{}", deviceDto.getSn());

    }
}
