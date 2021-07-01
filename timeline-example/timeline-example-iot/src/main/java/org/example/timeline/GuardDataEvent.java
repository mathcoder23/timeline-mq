package org.example.timeline;

/**
 * 门禁数据事件
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/1
 */
public interface GuardDataEvent {
    /**
     * 数据添加到组
     * @param guardDataId 数据id
     * @param groupId 组id
     */
    void onDataAddGroup(Long guardDataId,Long groupId);


    /**
     * 数据移除组
     * @param guardDataId 数据id
     * @param groupId 组id
     */
    void onDataDelGroup(Long guardDataId,Long groupId);


    /**
     * 数据删除
     * @param guardDataId 数据id
     */
    void onDataDel(Long guardDataId);

    /**
     * 数据修改
     * @param guardDataId 数据id
     */
    void onDataModify(Long guardDataId);
}
