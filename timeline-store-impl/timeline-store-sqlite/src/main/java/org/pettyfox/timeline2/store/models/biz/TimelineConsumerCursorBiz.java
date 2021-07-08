package org.pettyfox.timeline2.store.models.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.pettyfox.timeline2.store.models.entity.TimelineConsumerCursorRecord;
import org.pettyfox.timeline2.store.models.mapper.TimelineConsumerCursorMapper;
import org.springframework.stereotype.Service;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/8
 */
@Service
public class TimelineConsumerCursorBiz extends ServiceImpl<TimelineConsumerCursorMapper, TimelineConsumerCursorRecord> {
    public void removeConsumer(String consumerId) {
        LambdaQueryWrapper<TimelineConsumerCursorRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TimelineConsumerCursorRecord::getConsumerId, consumerId);
        remove(queryWrapper);
    }

    public boolean hasConsumer(String consumerId) {
        LambdaQueryWrapper<TimelineConsumerCursorRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TimelineConsumerCursorRecord::getConsumerId, consumerId);
        return count(queryWrapper) > 0;
    }

    public TimelineConsumerCursorRecord getCursor(String consumerId, String producerId) {
        LambdaQueryWrapper<TimelineConsumerCursorRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TimelineConsumerCursorRecord::getConsumerId, consumerId);
        queryWrapper.eq(TimelineConsumerCursorRecord::getProducerId, producerId);
        return getOne(queryWrapper);
    }
}
