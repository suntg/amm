package com.example.amm.controller;

import com.example.amm.job.MyTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

    private RedisTemplate<String, Object> redisTemplate;
    private MyTask myTask;

    @Autowired
    public SchedulerController(RedisTemplate<String, Object> redisTemplate, MyTask myTask) {
        this.redisTemplate = redisTemplate;
        this.myTask = myTask;
    }

    @GetMapping("/status")
    public Boolean getTaskStatus() {
        // 从Redis中获取任务状态
        return (Boolean) redisTemplate.opsForValue().get("task_enabled");
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