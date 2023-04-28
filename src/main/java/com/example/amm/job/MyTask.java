package com.example.amm.job;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.amm.CircularLinkedList;
import com.example.amm.domain.dto.AutoInfoDTO;
import com.example.amm.domain.entity.AccountDO;
import com.example.amm.domain.entity.TaskDO;
import com.example.amm.service.AccountService;
import com.example.amm.service.AutoService;
import com.example.amm.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.amm.util.DateTimeUtil.diffTime;

@Component
public class MyTask {

    private RedisTemplate<String, Object> redisTemplate;
    private CronSequenceGenerator cronGenerator;
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
        Boolean taskEnabled = (Boolean) redisTemplate.opsForValue().get("task_enabled");
        if (taskEnabled == null || !taskEnabled) {
            // 如果任务未启用，则直接返回
            return;
        }

        // 如果任务已启用，则执行任务逻辑
        if (taskService.count(new QueryWrapper<TaskDO>().lambda().ne(TaskDO::getStatus, 4).ne(TaskDO::getStatus, 6)) < 1) {
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
        List<AccountDO> accountDOList = accountService.list(new QueryWrapper<AccountDO>().lambda().ge(AccountDO::getBalance, 1)
                .gt(AccountDO::getGroup, 2));

        List<Integer> groupList = new ArrayList<>(); // 定义所有编组

        for (AccountDO v : accountDOList) {
            // 判断元素是否存在于列表中，如果不存在则加入
            if (!groupList.contains(v.getGroup())) {
                groupList.add(v.getGroup());  // 编组 作为元素加入列表
            }
        }

        int v = 0;

        for (Integer group : groupList) {
            autoInfoDTO.setGroup(group);
            // 发送号->有钱号
            if (accountService.count(new QueryWrapper<AccountDO>().lambda().eq(AccountDO::getGroup, group)) < 3) {
                continue;
            }

            AccountDO accountF = accountService.getOne(new QueryWrapper<AccountDO>().lambda().eq(AccountDO::getGroup, group)
                    .orderByDesc(AccountDO::getBalance)
                    .last(" LIMIT 1 "));
            if (accountF == null) {
                continue;
            }

            int ht = 24;

            autoInfoDTO.setId(accountF.getId());
            // 进行时间差值判断
            if (!diffTime(accountF.getUpdateTime(), ht * 60 * 60)) {

                msg = "[" + autoInfoDTO.getGroup() + "] 组 -> 账号 [" + accountF.getTitle() + "] " + accountF.getEmail()
                        + " 不满足" + ht + "小时条件！上次: " + accountF.getUpdateTime();
                autoService.setAutoLog(autoInfoDTO, msg);
                continue;
            }

            // 符合条件的继续执行

            // 接收号->最早更新号
            // TODO

            List<AccountDO> accountGroupList = accountService.list(new QueryWrapper<AccountDO>().lambda()
                    .eq(AccountDO::getGroup, group).ne(AccountDO::getTitle, "M")
                    .ne(AccountDO::getTitle, "X").orderByAsc(AccountDO::getTitle).select(AccountDO::getTitle));

            CircularLinkedList<String> list = new CircularLinkedList<>();
            for (AccountDO accountDO : accountGroupList) {
                list.insertWithOrder(accountDO.getTitle());
            }
            List<String> s = new ArrayList<>();
            for (int i = 0; i < list.getSize(); i++) {
                if (i == list.getSize() - 1) {
                    s.add(list.getNode(i) + "_" + list.getNode(0));
                } else {
                    s.add(list.getNode(i) + "_" + list.getNode(i + 1));
                }
            }
            String nextTitle = null;
            for (String s1 : s) {
                if (s1.startsWith(accountF.getTitle())) {
                    nextTitle = s1.split("_")[1];
                }
            }

            AccountDO accountT = accountService.getOne(new QueryWrapper<AccountDO>().lambda().eq(AccountDO::getGroup, group)
                    .eq(AccountDO::getTitle, nextTitle));
            //
            /*AccountDO accountT = accountService.getOne(
                    new QueryWrapper<AccountDO>().lambda()
                            .eq(AccountDO::getGroup, group)
                            .ne(AccountDO::getTitle, accountF.getTitle())
                            .orderByAsc(AccountDO::getUpdateTime)
                            .last("limit 1"));*/

            if (accountT == null) {
                continue;
            }

            // 判断转账金额
            // Random random = new Random();
            // int randomNumber = random.nextInt(2) + 1;

            // int money = Math.round(NumberUtil.sub(accountF.getBalance(), String.valueOf(randomNumber)).floatValue());
            // if (money > 12) {  // 限定最大
            //     money = 12;
            // }

            if (Double.parseDouble(accountF.getBalance()) < 1) {
                msg = "[" + group + "] 组 -> 账号 [" + accountF.getTitle() + "] " + accountF.getEmail() + " 金额不足！金额: " + accountF.getBalance();
                autoService.setAutoLog(autoInfoDTO, msg);
                continue;
            }

            String taskType = accountF.getTitle() + accountT.getTitle();

            if (taskType.length() != 2) {
                msg = "[" + group + "] 组 -> 账号 [" + accountF.getTitle() + "] " + accountF.getEmail() + " Type类型不符！Type: " + taskType;
                autoService.setAutoLog(autoInfoDTO, msg);
                continue;
            }

            TaskDO taskDO = new TaskDO();
            taskDO.setType(taskType);
            taskDO.setGroup(group);
            taskDO.setMoney(accountF.getBalance());
            taskDO.setRemark("[" + group + "] 组 -> 自动任务添加 " + LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
            taskService.save(taskDO);


            msg = "[" + group + "] 组 -> 任务 => [" + taskType + "] =>  金额：[" + taskDO.getMoney() + "] -> Auto添加完成！";
            autoInfoDTO.setTaskId(taskDO.getId());
            autoService.setAutoLog(autoInfoDTO, msg);

            v = v + 1;
        }


        autoInfoDTO.setId(0L);
        autoInfoDTO.setGroup(0);
        autoInfoDTO.setTaskId(0L);
        msg = "============== User => [" + user + "] 自动任务完成！ 新增任务：[ " + v + " ] ==============";
        autoService.setAutoLog(autoInfoDTO, msg);
    }
}