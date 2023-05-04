package com.example.amm.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.amm.DataProcessor;
import com.example.amm.common.BizException;
import com.example.amm.domain.dto.AutoInfoDTO;
import com.example.amm.domain.entity.AccountDO;
import com.example.amm.domain.entity.TaskDO;
import com.example.amm.service.AccountService;
import com.example.amm.service.AutoService;
import com.example.amm.service.TaskService;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MyTask {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CronSequenceGenerator cronGenerator;
    @Resource
    private AutoService autoService;

    @Resource
    private TaskService taskService;

    @Resource
    private AccountService accountService;

    @Autowired
    public MyTask(RedisTemplate<String, Object> redisTemplate, @Value("${task.cron}") String cron) {
        this.redisTemplate = redisTemplate;
        this.cronGenerator = new CronSequenceGenerator(cron);
    }

    @Scheduled(cron = "${task.cron}")
    public void doTask() {
        // 定时任务逻辑
        // 从Redis中读取任务状态
        Boolean taskEnabled = (Boolean)redisTemplate.opsForValue().get("task_enabled");
        if (taskEnabled == null || !taskEnabled) {
            // 如果任务未启用，则直接返回
            return;
        }

        // 如果任务已启用，则执行任务逻辑
        if (taskService
            .count(new QueryWrapper<TaskDO>().lambda().ne(TaskDO::getStatus, 4).ne(TaskDO::getStatus, 6)) < 1) {
            autoRun();
        }
    }

    public Date getNextExecutionTime() {
        return cronGenerator.next(new Date());
    }

    public void autoRun() {
        Integer user = 25;

        taskService.deleteAllTasks();
        String msg = "============================ Auto任务开始 ============================";

        AutoInfoDTO autoInfoDTO = new AutoInfoDTO();
        autoInfoDTO.setId(0L);
        autoInfoDTO.setGroup(0);
        autoInfoDTO.setTaskId(0L);
        autoInfoDTO.setUserId(user);
        autoService.setAutoLog(autoInfoDTO, msg);
        // 获取指定条件组的所有账号信息
        List<AccountDO> accountDOList = accountService
            .list(new QueryWrapper<AccountDO>().lambda().ge(AccountDO::getBalance, 1).gt(AccountDO::getGroup, 2));

        List<Integer> groupList = new ArrayList<>(); // 定义所有编组

        for (AccountDO v : accountDOList) {
            // 判断元素是否存在于列表中，如果不存在则加入
            if (!groupList.contains(v.getGroup())) {
                groupList.add(v.getGroup()); // 编组 作为元素加入列表
            }
        }

        int v;

        try {
            DataProcessor processor =
                new DataProcessor(groupList, 50, autoInfoDTO, autoService, taskService, accountService); // 每批次查询 100
                                                                                                         // 条数据
            v = processor.process();
        } catch (InterruptedException e) {
            log.error("批次生成线程异常:{}", ExceptionUtil.stacktraceToString(e));
            throw new BizException("批次生成线程异常");
        }
        autoInfoDTO.setId(0L);
        autoInfoDTO.setGroup(0);
        autoInfoDTO.setTaskId(0L);
        msg = "============== User => [" + user + "] 自动任务完成！ 新增任务：[ " + v + " ] ==============";
        autoService.setAutoLog(autoInfoDTO, msg);
    }
}