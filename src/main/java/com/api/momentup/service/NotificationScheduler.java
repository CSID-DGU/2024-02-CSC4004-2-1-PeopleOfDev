package com.api.momentup.service;

import ch.qos.logback.core.util.TimeUtil;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.*;

@Component
public class NotificationScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();



    /**
     * 5분 뒤 실행되는 작업 등록
     * @param notificationId 알림 ID
     * @param task 실행할 작업
     */
    public void scheduleTask(Long alarmNumber, Runnable task) {
        ScheduledFuture<?> scheduledFuture = scheduler.schedule(task, 5, TimeUnit.MINUTES);
        System.out.println("Scheduled task to run in 5 minutes for notification ID: " + alarmNumber);
        scheduledTasks.put(alarmNumber, scheduledFuture);
    }

    /**
     * 등록된 작업 취소
     * @param notificationId 알림 ID
     */
    public void cancelTask(Long notificationId) {
        ScheduledFuture<?> scheduledFuture = scheduledTasks.remove(notificationId);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }

    /**
     * 모든 작업 종료 (애플리케이션 종료 시 사용)
     */
    public void shutdown() {
        System.out.println("Scheduler is shut down. Reinitializing...");
        scheduler.shutdown();
    }
}
