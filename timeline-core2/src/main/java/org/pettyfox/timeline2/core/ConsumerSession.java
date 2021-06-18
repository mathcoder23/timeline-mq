package org.pettyfox.timeline2.core;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/6/18 10:54
 */
@Getter
@Setter
public class ConsumerSession {
    private String consumerId;
    private int batchSize;
    private TimelineMqConsumerListener listener;
    /**
     * 是否休眠
     */
    private volatile boolean sleep;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConsumerSession that = (ConsumerSession) o;
        return this.consumerId.equals(that.consumerId);
    }

    @Override
    public int hashCode() {
        int h;
        return (h = consumerId.hashCode()) ^ (h >>> 16);
    }
}
