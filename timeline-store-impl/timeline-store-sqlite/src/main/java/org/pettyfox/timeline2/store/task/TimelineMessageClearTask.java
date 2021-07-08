package org.pettyfox.timeline2.store.task;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.pettyfox.timeline2.store.models.biz.TimelineMessageBiz;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/8
 */
@Component
@Slf4j
public class TimelineMessageClearTask {
    private final ScheduledExecutorService SCHEDULE_TASK = new ScheduledThreadPoolExecutor(1,
            ThreadFactoryBuilder.create().setNamePrefix("job-").build(), (r, executor) -> {
        log.warn("job exception");
    });
    public static final AtomicInteger CLEAR_FLAG = new AtomicInteger(1);
    @Resource
    private TimelineMessageBiz timelineMessageBiz;

    @PostConstruct
    public void start() {
        SCHEDULE_TASK.scheduleWithFixedDelay(() -> {
            try {
                clearOptimizeFlag();
            } catch (Exception e) {
                log.error("clear optimize flag error", e);
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    private void clearOptimizeFlag() {
        int cursor = CLEAR_FLAG.get();
        if (cursor <= 0) {
            return;
        }
        timelineMessageBiz.clearOptimizeFlag();
        CLEAR_FLAG.addAndGet(-cursor);

    }
}
