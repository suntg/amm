package com.example.amm.controller;

import com.example.amm.job.AutoJob;
import com.example.amm.service.AutoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ScheduledFuture;

@Tag(name = "Job")
@Slf4j
@RestController
@RequestMapping("/job")
public class JobController {

    @Resource
    private AutoJob autoJob;

    // @GetMapping("/start")
    // public String startTask() {
    //     autoJob.startTask();
    //     return "定时任务已启动";
    // }
    //
    // @GetMapping("/stopTask")
    // public String stopTask() {
    //     autoJob.stopTask();
    //     return "定时任务已停止";
    // }
}
