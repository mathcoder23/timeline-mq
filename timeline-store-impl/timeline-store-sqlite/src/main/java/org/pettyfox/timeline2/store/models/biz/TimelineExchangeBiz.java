package org.pettyfox.timeline2.store.models.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.pettyfox.timeline2.store.models.entity.TimelineExchange;
import org.pettyfox.timeline2.store.models.mapper.TimelineExchangeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/8
 */
@Service
public class TimelineExchangeBiz extends ServiceImpl<TimelineExchangeMapper, TimelineExchange> {
    public boolean hasConsumerBindProducer(String consumerId, String producerId) {
        LambdaQueryWrapper<TimelineExchange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TimelineExchange::getConsumerId, consumerId);
        queryWrapper.eq(TimelineExchange::getProducerId, producerId);
        return count(queryWrapper) > 0;
    }

    public void unbind(String consumerId, String producerId) {
        LambdaQueryWrapper<TimelineExchange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TimelineExchange::getConsumerId, consumerId);
        queryWrapper.eq(TimelineExchange::getProducerId, producerId);
        remove(queryWrapper);
    }

    public void unbindConsumerId(String consumerId) {
        LambdaQueryWrapper<TimelineExchange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TimelineExchange::getConsumerId, consumerId);
        remove(queryWrapper);
    }

    public void unbindProducerId(String producerId) {
        LambdaQueryWrapper<TimelineExchange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TimelineExchange::getProducerId, producerId);
        remove(queryWrapper);
    }

    public List<TimelineExchange> listByConsumer(String consumerId) {
        LambdaQueryWrapper<TimelineExchange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TimelineExchange::getConsumerId, consumerId);
        return list(queryWrapper);
    }

    public List<TimelineExchange> listByProducer(String producerId) {
        LambdaQueryWrapper<TimelineExchange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TimelineExchange::getProducerId, producerId);
        return list(queryWrapper);
    }
}
