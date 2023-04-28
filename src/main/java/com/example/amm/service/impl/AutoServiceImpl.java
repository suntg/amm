package com.example.amm.service.impl;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.amm.constant.BusinessType;
import com.example.amm.constant.User;
import com.example.amm.domain.dto.AutoInfoDTO;
import com.example.amm.domain.entity.LogDO;
import com.example.amm.domain.entity.TaskDO;
import com.example.amm.service.AutoService;
import com.example.amm.service.LogService;
import com.example.amm.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AutoServiceImpl implements AutoService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private TaskService taskService;
    @Resource
    private LogService logService;

    @Transactional(rollbackFor = Exception.class)
    @Async
    public void setAutoLog(AutoInfoDTO info, String logs) {
        StringBuilder value = new StringBuilder();
        value.append("[");
        value.append(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        value.append("]");
        value.append(" => ");
        value.append(logs);


        List<LogDO> logDOList = new ArrayList<>();


        LogDO logDO = new LogDO();
        logDO.setBusinessId(User.USER_ID_25);
        logDO.setMessage(String.valueOf(value));
        logDO.setBusiness(BusinessType.AUTO_USER.toString());
        logDO.setLogTime(LocalDateTimeUtil.now());

        logDOList.add(logDO);
        // logService.save(logDO);


        // String key = "autopp_" + info.getUserId() + "_autologs";
        //
        // redisTemplate.opsForList().leftPush(key, String.valueOf(value));
        // redisTemplate.expire(key, 30, TimeUnit.DAYS);


        // 写入账号日志
        if (info.getId() > 0) {
            // logDO.setId(null);
            LogDO log = new LogDO();
            log.setMessage(String.valueOf(value));
            log.setLogTime(LocalDateTimeUtil.now());
            log.setBusinessId(String.valueOf(info.getId()));
            log.setBusiness(BusinessType.AUTO_ACCOUNT.toString());

            logDOList.add(log);
            // logService.save(logDO);
            // key = "autopp_accountlog_" + info.getId();
            // redisTemplate.opsForList().leftPush(key, String.valueOf(value));
            // redisTemplate.expire(key, 30, TimeUnit.DAYS);
        }

        // 写入Group日志
        if (info.getGroup() > 0) {
            // logDO.setId(null);
            LogDO log = new LogDO();
            log.setMessage(String.valueOf(value));
            log.setLogTime(LocalDateTimeUtil.now());
            log.setBusinessId(String.valueOf(info.getGroup()));
            log.setBusiness(BusinessType.AUTO_GROUP.toString());

            logDOList.add(log);
            // logService.save(logDO);

            // key = "autopp_grouplog_" + info.getGroup();
            // redisTemplate.opsForList().leftPush(key, String.valueOf(value));
            // redisTemplate.expire(key, 90, TimeUnit.DAYS);
        }


        // 写入Task日志
        if (info.getTaskId() > 0) {  // 任务添加成功的情况下
            // 写入Task日志 -> redis
            // logDO.setId(null);
            LogDO log = new LogDO();
            log.setMessage(String.valueOf(value));
            log.setLogTime(LocalDateTimeUtil.now());
            log.setBusinessId(String.valueOf(info.getTaskId()));
            log.setBusiness(BusinessType.AUTO_TASK.toString());
            logDOList.add(log);
            // logService.save(logDO);

            // key = "autopp_tasklog_" + info.getTaskId();
            // redisTemplate.opsForList().leftPush(key, String.valueOf(value));
            // redisTemplate.expire(key, 1, TimeUnit.DAYS);
            // 最后一条信息更新到 任务面板lastlog备注

            UpdateWrapper<TaskDO> wrapper = new UpdateWrapper<>();
            wrapper.lambda().set(TaskDO::getRemark, String.valueOf(value)).eq(TaskDO::getId, info.getTaskId());

            taskService.update(null, wrapper);
        }

        logService.saveBatch(logDOList);
    }

}
