package org.example.websocket;

import org.example.dto.DeviceDto;
import org.example.timeline.TimelineDeviceService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/1
 */
@Component
public class DeviceSessionManager {
    private final ConcurrentHashMap<String, DeviceDto> session = new ConcurrentHashMap<>();
    @Resource
    private TimelineDeviceService timelineDeviceService;

    public void onlineDevice(DeviceDto deviceDto) {
        session.put(deviceDto.getSn(), deviceDto);
        timelineDeviceService.deviceOnline(deviceDto);
    }

    public void offlineDevice(String sn) {
        DeviceDto deviceDto = session.get(sn);
        if (null != deviceDto) {
            session.remove(sn);
            timelineDeviceService.deviceOffline(deviceDto);
        }
    }

    public boolean hasOnline(String sn) {
        return session.containsKey(sn);
    }
}
