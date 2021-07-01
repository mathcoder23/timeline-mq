package org.example.timeline;

import org.example.dto.DeviceDto;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/1
 */
public interface TimelineDeviceService {
    void deviceOnline(DeviceDto deviceDto);
    void deviceOffline(DeviceDto deviceDto);
}
