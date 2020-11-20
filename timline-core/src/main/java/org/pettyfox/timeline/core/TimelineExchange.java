package org.pettyfox.timeline.core;

import java.util.List;

/**
 *订阅者、被订阅者绑定
 * @author Petty Fox
 * @version 1.0
 */
public interface TimelineExchange {
    /**
     * 订阅
     * @param consumerId 订阅者id
     * @param producerIds 被订阅者id 列表
     */
    void subscribe(String consumerId,String ... producerIds);

    /**
     * 取消订阅
     * @param consumerId 订阅者id
     * @param producerId 被订阅者id
     */
    void unsubscribe(String consumerId,String producerId);

    /**
     * 订阅者取消所有订阅
     * @param consumerId 订阅者id
     */
    void removeAllSubscribe(String consumerId);

    /**
     * 被订阅者取消所有订阅者
     * @param producerId 被订阅者id
     */
    void removeAllSubscribeByBeSubscribe(String producerId);

    /**
     * 列出订阅者
     * @param producerId 被订阅者id
     * @return 订阅者列表
     */
    List<String> listBySubscribe(String producerId);

    /**
     * 列出被订阅者
     * @param consumerId 订阅者id
     * @return 被订阅者列表
     */
    List<String> listByBeSubscribe(String consumerId);

}
