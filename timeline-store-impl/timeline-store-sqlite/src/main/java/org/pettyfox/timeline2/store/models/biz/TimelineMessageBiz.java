package org.pettyfox.timeline2.store.models.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.pettyfox.timeline2.store.models.entity.TimelineMessageRecord;
import org.pettyfox.timeline2.store.models.mapper.TimelineMessageMapper;
import org.pettyfox.timeline2.store.task.TimelineMessageClearTask;
import org.springframework.stereotype.Service;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/8
 */
@Service
public class TimelineMessageBiz extends ServiceImpl<TimelineMessageMapper, TimelineMessageRecord> {
    public void updateOptimizeData(TimelineMessageRecord timelineMessageEntity) {
        if (null == timelineMessageEntity.getObjId()) {
            return;
        }
        TimelineMessageClearTask.CLEAR_FLAG.incrementAndGet();
        LambdaUpdateWrapper<TimelineMessageRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TimelineMessageRecord::getTopic, timelineMessageEntity.getTopic());
        updateWrapper.eq(TimelineMessageRecord::getObjId, timelineMessageEntity.getObjId());
        updateWrapper.set(TimelineMessageRecord::getOptimizeFlag, true);
        update(updateWrapper);
    }
    public void clearOptimizeFlag(){
        LambdaQueryWrapper<TimelineMessageRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TimelineMessageRecord::getOptimizeFlag,true);
        remove(queryWrapper);
    }
}
