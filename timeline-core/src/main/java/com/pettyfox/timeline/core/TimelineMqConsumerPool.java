package com.pettyfox.timeline.core;

import lombok.extern.slf4j.Slf4j;
import com.pettyfox.timeline.model.TimelineMessage;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/6/18 10:22
 */
@Slf4j
public class TimelineMqConsumerPool {
    private static final int HASH_BITS = 0x7fffffff;
    private static final String THREAD_NAME_PREFIX = "timeline-mq-cpool-";
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
            pool[i].setName(THREAD_NAME_PREFIX + i);
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

    public void wakeupThread(String consumerId) {
        int hash = spread(consumerId.hashCode());
        pool[hash % pool.length].wakeupThreadByConsumerId(consumerId);
    }

    private final class WorkThread extends Thread {
        private final ConcurrentHashMap<String, ConsumerSession> consumerMap = new ConcurrentHashMap<>();
        private final AtomicBoolean allSleep = new AtomicBoolean(true);
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition condition = lock.newCondition();
        private volatile boolean work = true;

        public void addConsumer(ConsumerSession consumerSession) {
            consumerMap.put(consumerSession.getConsumerId(), consumerSession);
            wakeupThread();
        }

        public void removeConsumer(String consumerId) {
            consumerMap.remove(consumerId);
            if (consumerMap.isEmpty()) {
                allSleep.set(true);
            }
        }

        public void wakeupThread() {
            lock.lock();
            try {
                condition.signal();
            } finally {
                lock.unlock();
            }
        }

        public void wakeupThreadByConsumerId(String consumerId) {
            if (!consumerMap.containsKey(consumerId)) {
                return;
            }
            consumerMap.get(consumerId).setSleep(false);
            wakeupThread();
        }

        @Override
        public void run() {
            while (work) {
                try {
                    if (allSleep.get()) {
                        lock.lock();
                        try {
                            condition.await();
                        } finally {
                            lock.unlock();
                        }
                    }
                    consumerMap.forEach((k, consumer) -> {
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
                    if (null != consumer.getListener()) {
                        consumer.getListener().batchConsumer(queue, consumer.getConsumerId());
                    } else {
                        log.warn("not config consumer listener.");
                    }
                }
            }
        }
    }
}
