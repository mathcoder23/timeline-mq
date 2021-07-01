package org.example.timeline;

/**
 * 门禁权限组与设备事件
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/1
 */
public interface GuardGroupDeviceEvent {
    /**
     * 添加到组
     * @param deviceId 设备id
     * @param groupId 组id
     */
    void onAddGroup(Long deviceId,Long groupId);


    /**
     * 设备在组中移除
     * @param deviceId 设备id
     * @param groupId 组id
     */
    void onDelGroup(Long deviceId,Long groupId);
}
