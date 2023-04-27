package com.example.amm.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;


@Component
public class AutoJob {

    @Autowired
    private TaskScheduler taskScheduler;

    private ScheduledFuture<?> scheduledTask;

    @Scheduled(fixedRate = 1000)
    public void doTask() {
        System.out.println("定时任务执行");
    }

    public void startTask() {
        if (scheduledTask == null || scheduledTask.isCancelled()) {
            scheduledTask = taskScheduler.schedule(this::doTask, new CronTrigger("*/1 * * * * *"));
            System.out.println("定时任务已启动");
        }
    }

    public void stopTask() {
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            System.out.println("定时任务已停止");
        }
    }
}
