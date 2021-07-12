package org.example.timeline.event;

/**
 * 门禁数据事件
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/1
 */
public interface GuardGroupEvent {

    /**
     * 数据删除
     */
    void onDelGroup(String groupId);
}
