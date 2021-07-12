package org.example.timeline.event;

/**
 * 门禁权限组与设备事件
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/1
 */
public interface GuardGroupDeviceEvent {
    /**
     * 添加到组
     * @param deviceSn 设备id
     * @param groupId 组id
     */
    void onAddGroup(String deviceSn,Long groupId);


    /**
     * 设备在组中移除
     * @param deviceSn 设备id
     * @param groupId 组id
     */
    void onDelGroup(String deviceSn,Long groupId);
}
