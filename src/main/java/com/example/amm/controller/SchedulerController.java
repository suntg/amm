package com.example.amm.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.amm.job.MyTask;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Scheduler")
@Slf4j
@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final MyTask myTask;

    @Autowired
    public SchedulerController(RedisTemplate<String, Object> redisTemplate, MyTask myTask) {
        this.redisTemplate = redisTemplate;
        this.myTask = myTask;
    }

    @GetMapping("/status")
    public Boolean getTaskStatus() {
        // 从Redis中获取任务状态
        return (Boolean)redisTemplate.opsForValue().get("task_enabled");
    }

    @GetMapping("/next-execution-time")
    public Date getNextExecutionTime() {
        // 获取下次执行时间
        return myTask.getNextExecutionTime();
    }

    @GetMapping("/enable")
    public String enableTask() {
        // 启用定时任务
        redisTemplate.opsForValue().set("task_enabled", true);
        return "Task enabled";
    }

    @GetMapping("/disable")
    public String disableTask() {
        // 停用定时任务
        redisTemplate.opsForValue().set("task_enabled", false);
        return "Task disabled";
    }

}