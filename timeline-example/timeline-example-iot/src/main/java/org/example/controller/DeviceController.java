package org.example.controller;

import org.example.dto.DeviceDto;
import org.example.timeline.event.GuardGroupDeviceEvent;
import org.example.websocket.DeviceSessionManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 设备管理
 *
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/1
 */
@RestController
@RequestMapping("/device")
public class DeviceController {
    @Resource
    private GuardGroupDeviceEvent guardGroupDeviceEvent;
    @Resource
    private DeviceSessionManager deviceSessionManager;

    @GetMapping("deviceAddGroup")
    public String deviceAddGroup(@RequestParam String deviceSn, @RequestParam Long guardGroupId) {
        guardGroupDeviceEvent.onAddGroup(deviceSn, guardGroupId);
        return "";
    }

    @GetMapping("deviceDelGroup")
    public String deviceDelGroup(@RequestParam String deviceSn, @RequestParam Long guardGroupId) {
        guardGroupDeviceEvent.onDelGroup(deviceSn, guardGroupId);
        return "";
    }

    @GetMapping("deviceList")
    public List<DeviceDto> deviceDelGroup() {
        return deviceSessionManager.list();
    }
}
