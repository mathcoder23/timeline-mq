package org.pettyfox.timeline2.core;

import org.pettyfox.timeline2.model.TimelineMessage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/6/18 10:22
 */
public class TimelineMqConsumerPool {
    private static final int HASH_BITS = 0x7fffffff;
    private int currentCount = 2;
    private final TimelineCursorMq timelineCursorMq;
    private WorkThread[] pool;

    public TimelineMqConsumerPool(TimelineCursorMq timelineCursorMq) {
        initWorkThread();
        this.timelineCursorMq = timelineCursorMq;
    }

    private void initWorkThread() {
        pool = new WorkThread[currentCount];
        for (int i = 0; i < pool.length; i++) {
            pool[i] = new WorkThread();
            pool[i].setName("timeline-mq-cpool-" + i);
            pool[i].start();
        }
    }

    public void addConsumer(ConsumerSession consumerSession) {
        int hash = spread(consumerSession.getConsumerId().hashCode());
        pool[hash % pool.length].addConsumer(consumerSession);
    }

    public void removeConsumer(String consumerId) {
        int hash = spread(consumerId.hashCode());
        pool[hash % pool.length].removeConsumer(consumerId);
    }


    static int spread(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }

    private final class WorkThread extends Thread {
        private final CopyOnWriteArraySet<ConsumerSession> consumerList = new CopyOnWriteArraySet<>();
        private final AtomicBoolean allSleep = new AtomicBoolean(true);
        private final Condition condition = new ReentrantLock().newCondition();

        public void addConsumer(ConsumerSession consumerSession) {
            consumerList.remove(consumerSession);
            consumerList.add(consumerSession);
            wakeupThread();
        }

        public void removeConsumer(String consumerId) {
            ConsumerSession consumerSession = new ConsumerSession();
            consumerSession.setConsumerId(consumerId);
            consumerList.remove(consumerSession);
            if (consumerList.isEmpty()) {
                allSleep.set(true);
            }
        }

        public void wakeupThread() {
            synchronized (condition) {
                condition.notify();
            }
        }

        @Override
        public void run() {
            for (; ; ) {
                try {
                    if (allSleep.get()) {
                        synchronized (condition) {
                            condition.wait();
                        }
                    }
                    consumerList.forEach(consumer -> {
                        if (!consumer.isSleep()) {
                            allSleep.set(false);
                            dispatchConsumer(consumer);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void dispatchConsumer(ConsumerSession consumer) {
            if (!consumer.isSleep()) {
                consumer.setSleep(true);
                List<TimelineMessage> queue = timelineCursorMq.pull(consumer.getConsumerId(), consumer.getBatchSize());
                if (null != queue && !queue.isEmpty()) {
                    consumer.getListener().batchConsumer(queue);
                }
            }
        }
    }
}
