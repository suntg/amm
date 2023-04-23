package com.example.amm.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.amm.constant.RedisKeyConstant;
import com.example.amm.domain.entity.AccountDO;
import com.example.amm.domain.entity.TaskDO;
import com.example.amm.domain.query.PageQuery;
import com.example.amm.mapper.TaskMapper;
import com.example.amm.service.AccountService;
import com.example.amm.service.TaskService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, TaskDO> implements TaskService {

    @Resource
    private TaskMapper taskMapper;
    @Resource
    private AccountService accountService;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean saveTask(TaskDO taskDO) {
        return taskMapper.insert(taskDO) > 0;
    }

    @Override
    public boolean updateTaskById(Long id, TaskDO taskDO) {
        taskDO.setId(id);
        return taskMapper.updateById(taskDO) > 0;
    }

    @Override
    public boolean removeTaskById(Long id) {
        return taskMapper.deleteById(id) > 0;
    }

    @Override
    public TaskDO getTaskById(Long id) {
        return taskMapper.selectById(id);
    }

    @Override
    public Page<TaskDO> listPage(PageQuery pageQuery) {
        return this.page(new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()), new QueryWrapper<TaskDO>().lambda()
                .orderByDesc(TaskDO::getCreateTime));
    }

    @Override
    public void createQuickTask(TaskDO task) {
        taskMapper.insert(task);
    }

    @Override
    public void uploadLog(Long id, String log) {
        String value = "[" +
                LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN) +
                "]" +
                " => " +
                log;


        redisTemplate.opsForList().leftPush(RedisKeyConstant.TASK_LOG_KEY + id, value);
        redisTemplate.expire(RedisKeyConstant.TASK_LOG_KEY + id, 24, TimeUnit.HOURS);

        LambdaUpdateWrapper<TaskDO> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(TaskDO::getId, id)
                .set(TaskDO::getRemark, log);
        taskMapper.update(null, lambdaUpdateWrapper);
    }

    @Override
    public int setTaskStatus(Long id, int status) {
        TaskDO task = taskMapper.selectById(id);
        String type = task.getType();

        task.setStatus(status);
        taskMapper.updateById(task);
        AccountDO fromAccount;
        AccountDO toAccount;
        if (type.length() == 2) {
            // AB模式
            char from = type.charAt(0);
            char to = type.charAt(1);

            fromAccount = accountService.getOne(new QueryWrapper<AccountDO>().lambda()
                    .eq(AccountDO::getEmail, task.getGroup()).eq(AccountDO::getTitle, from));
            toAccount = accountService.getOne(new QueryWrapper<AccountDO>().lambda()
                    .eq(AccountDO::getEmail, task.getGroup()).eq(AccountDO::getTitle, to));

        } else {
            String[] arr = type.split("->");
            String from = arr[0];
            String to = arr[1];
            fromAccount = accountService.getById(Integer.parseInt(from));
            toAccount = accountService.getById(Integer.parseInt(to));
        }
        if (status == 6) { // 成功
            if ("X".equals(toAccount.getTitle())) {
                Integer newGroup;
                String newTitle;
                if ("M".equals(fromAccount.getTitle())) {
                    newTitle = "A";
                    newGroup = accountService.getNewGroup();
                } else if ("A".equals(fromAccount.getTitle())) {
                    newTitle = "B";
                    newGroup = fromAccount.getGroup();
                } else if ("B".equals(fromAccount.getTitle())) {
                    newTitle = "C";
                    newGroup = fromAccount.getGroup();
                } else {
                    newTitle = "错误";
                    newGroup = 1;
                }
                accountService.update(new AccountDO().setGroup(newGroup).setTitle(newTitle),
                        new UpdateWrapper<AccountDO>().lambda().eq(AccountDO::getId, toAccount.getId()));

            }

        }
        task = taskMapper.selectById(id);
        return task.getStatus();
    }

    @Override
    public int getTaskStatus(Long id) {
        TaskDO taskDO = taskMapper.selectById(id);
        return taskDO.getStatus();
    }


    @Override
    public void deleteSucTasks() {
        taskMapper.delete(new QueryWrapper<TaskDO>().lambda().eq(TaskDO::getStatus, 6));
    }

    @Override
    public void deleteAllTasks() {
        int user = 25;
        List<TaskDO> taskList = taskMapper.selectList(Wrappers.<TaskDO>lambdaQuery().select(TaskDO::getId));
        // 循环删除
        for (TaskDO task : taskList) {
            taskMapper.deleteById(task.getId()); // SQL删除
            // autopp_tasklog_
            redisTemplate.delete(RedisKeyConstant.AUTO_TASK_LOG_KEY + task.getId());
            // Redis移除队列
            remRedisQueue(task.getId(), user);
        }
        // Redis直接删除队列Key
        // autopp_25_taskqueue
        String key = "autopp_" + user + "_taskqueue";
        redisTemplate.delete(key);

        // 清空自动日志的redis
        redisTemplate.delete("autopp_25_autologs");
    }


    public void remRedisQueue(Long id, Integer user) {
        // autopp_25_taskqueue
        String key = "autopp_" + user + "_taskqueue";
        redisTemplate.opsForList().remove(key, 1, id);
    }

    @Override
    public void executeTask(Long id) {
        List<String> taskQueue = redisTemplate.opsForList().range("auto_25_taskqueue", 0, -1);
        if (CollUtil.isEmpty(taskQueue) || (CollUtil.isNotEmpty(taskQueue) && !taskQueue.contains(String.valueOf(id)))) {
            redisTemplate.opsForList().leftPush("auto_25_taskqueue", String.valueOf(id));
        }
        updateStatus(id, 0);
    }

    @Override
    public void updateStatus(Long id, int status) {
        TaskDO task = new TaskDO();
        task.setStatus(0);
        LambdaUpdateWrapper<TaskDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TaskDO::getId, id);
        taskMapper.update(task, updateWrapper);
    }

    @Override
    public void stopTask(Long id) {
        redisTemplate.opsForList().remove("auto_25_taskqueue", 0, id);
        updateStatus(id, 8);
    }
}
