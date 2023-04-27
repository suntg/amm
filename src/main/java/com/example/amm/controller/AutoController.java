package com.example.amm.controller;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.amm.common.BizException;
import com.example.amm.domain.dto.AutoInfoDTO;
import com.example.amm.domain.entity.AccountDO;
import com.example.amm.domain.entity.TaskDO;
import com.example.amm.domain.vo.AccountVO;
import com.example.amm.service.AccountService;
import com.example.amm.service.AutoService;
import com.example.amm.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Tag(name = "Auto")
@Slf4j
@RestController
@RequestMapping("/auto")
public class AutoController {

    @Resource
    private AutoService autoService;

    @Resource
    private TaskService taskService;

    @Resource
    private AccountService accountService;

    @Operation(summary = "自动养号逻辑 autorun()")
    @GetMapping("/autoRun")
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
        List<AccountDO> accountDOList = accountService.list(new QueryWrapper<AccountDO>().lambda().gt(AccountDO::getBalance, 1)
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

            AccountDO accountF = accountService.getOne(new QueryWrapper<AccountDO>().lambda().eq(AccountDO::getGroup, group)
                    .orderByDesc(AccountDO::getBalance)
                    .last(" LIMIT 1 "));
            if (accountF == null) {
                continue;
            }

            int ht = 24;

            autoInfoDTO.setId(accountF.getId());
            if (!diffTime(accountF.getUpdateTime(), ht * 60 * 60)) {

                msg = "[" + autoInfoDTO.getGroup() + "] 组 -> 账号 [" + accountF.getTitle() + "] " + accountF.getEmail()
                        + " 不满足" + ht + "小时条件！上次: " + accountF.getUpdateTime();
                autoService.setAutoLog(autoInfoDTO, msg);
                continue;
            }

            // 符合条件的继续执行/////////////////////

            // 接收号->最早更新号

            AccountDO accountT = accountService.getOne(
                    new QueryWrapper<AccountDO>().lambda()
                            .eq(AccountDO::getGroup, group)
                            .ne(AccountDO::getTitle, accountF.getTitle())
                            .orderByAsc(AccountDO::getUpdateTime)
                            .last("limit 1"));

            if (accountT == null) {
                continue; // Skip to next group
            }

            // Calculate task amount based on balance of first account
            int money = Math.round(NumberUtil.sub(accountF.getBalance(), String.valueOf(RandomUtil.randomInt(1, 3))).floatValue());
            if (money > 12) {  // 限定最大
                money = 12;
            }

            if (money < 1) { // Amount is less than 1
                // Log message and skip to next group
                msg = "[" + group + "] 组 -> 账号 [" + accountF.getTitle() + "] " + accountF.getEmail() + " 金额不足！金额: " + accountF.getBalance();
                autoService.setAutoLog(autoInfoDTO, msg);
                continue;
            }

            // Set task type
            String taskType = accountF.getTitle() + accountT.getTitle();

            if (taskType.length() != 2) { // Type length is not 2
                // Log message and skip to next group
                msg = "[" + group + "] 组 -> 账号 [" + accountF.getTitle() + "] " + accountF.getEmail() + " Type类型不符！Type: " + taskType;
                autoService.setAutoLog(autoInfoDTO, msg);
                continue;
            }

            TaskDO taskDO = new TaskDO();
            taskDO.setType(taskType);
            taskDO.setGroup(group);
            taskDO.setMoney(String.valueOf(money));
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


    @Operation(summary = "自动养号逻辑 autoxxx($do)")
    @GetMapping("/autoXXX")
    public void autoXXX(String doParam) {
        String doParamUpper = doParam.toUpperCase(); // 转大写
        int user = 25;
        taskService.deleteAllTasks();

        AutoInfoDTO autoInfoDTO = new AutoInfoDTO();
        autoInfoDTO.setId(0L);
        autoInfoDTO.setGroup(0);
        autoInfoDTO.setTaskId(0L);
        autoInfoDTO.setUserId(user);

        String msg = "============================ Auto" + doParamUpper + "任务开始 ============================";
        autoService.setAutoLog(autoInfoDTO, msg);

        List<Long> allFrom = new ArrayList<>();
        if ("MX".equals(doParamUpper)) {
            // 寻找 > 1的  M号
            List<AccountDO> accountDOList = accountService.list(
                    new QueryWrapper<AccountDO>().lambda().gt(AccountDO::getBalance, 1)
                            .eq(AccountDO::getGroup, 1).eq(AccountDO::getTitle, "M").select(AccountDO::getId));

            // 建立From号 队列
            for (AccountDO account : accountDOList) {
                // 判断元素是否存在于列表中，如果不存在则加入
                if (!allFrom.contains(account.getId())) {
                    allFrom.add(account.getId());
                }
            }


        } else if ("AX".equals(doParamUpper)) {
            // 寻找 > 1的  A号
            List<AccountDO> accountDOList = accountService.list(
                    new QueryWrapper<AccountDO>().lambda().gt(AccountDO::getBalance, 1)
                            .eq(AccountDO::getTitle, "A").select(AccountDO::getGroup));

            // 建立From号 队列
            for (AccountDO account : accountDOList) {
                // 判断元素是否存在于列表中，如果不存在则加入
                if (!allFrom.contains(Long.valueOf(account.getGroup()))) {
                    allFrom.add(Long.valueOf(account.getGroup()));
                }
            }
        } else if ("BX".equals(doParamUpper)) {
            // 寻找 > 1的  A号
            List<AccountDO> accountDOList = accountService.list(
                    new QueryWrapper<AccountDO>().lambda().gt(AccountDO::getBalance, 1)
                            .eq(AccountDO::getTitle, "B").select(AccountDO::getGroup));

            // 建立From号 队列
            for (AccountDO account : accountDOList) {
                // 判断元素是否存在于列表中，如果不存在则加入
                if (!allFrom.contains(Long.valueOf(account.getGroup()))) {
                    allFrom.add(Long.valueOf(account.getGroup()));
                }
            }
        }
        int v = 0;

        for (Long from : allFrom) {
            AccountDO accountF = null;
            if (!"MX".equals(doParamUpper)) {
                if ("AX".equals(doParamUpper)) {
                    // 发送号
                    AccountDO accountTmp = accountService.getOne(new QueryWrapper<AccountDO>().lambda()
                            .eq(AccountDO::getGroup, from).eq(AccountDO::getTitle, "A"));
                    if (accountTmp == null) {
                        continue; // 跳过
                    }
                    accountF = accountTmp;

                    // 确保该组不存在 B
                    AccountDO accountX = accountService.getOne(new QueryWrapper<AccountDO>().lambda()
                            .eq(AccountDO::getGroup, from).eq(AccountDO::getTitle, "B").select(AccountDO::getId));
                    if (accountX != null) {  // 如果存在 则 跳过
                        continue; // 跳过
                    }
                }
                if ("BX".equals(doParamUpper)) {
                    // 发送号
                    AccountDO accountTmp = accountService.getOne(new QueryWrapper<AccountDO>().lambda()
                            .eq(AccountDO::getGroup, from).eq(AccountDO::getTitle, "B"));
                    if (accountTmp == null) {
                        continue; // 跳过
                    }
                    accountF = accountTmp;

                    // 确保该组不存在 C
                    AccountDO accountX = accountService.getOne(new QueryWrapper<AccountDO>().lambda()
                            .eq(AccountDO::getGroup, from).eq(AccountDO::getTitle, "C").select(AccountDO::getId));
                    if (accountX != null) {  // 如果存在 则 跳过
                        continue; // 跳过
                    }

                }

                if (!diffTime(accountF.getUpdateTime(), 24 * 3600)) {
                    msg = "[" + from + "] 组 -> 账号 [" + accountF.getTitle() + "] " + accountF.getEmail()
                            + " 不满足24小时条件！上次: " + accountF.getUpdateTime();
                    autoService.setAutoLog(autoInfoDTO, msg);
                    continue;
                }


            }

            Long x = getX();
            if (x == 0) {
                // 写入日志
                String value = "[" +
                        LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN) +
                        "]" +
                        " => " +
                        "[没有X号了] -> Auto" +
                        doParamUpper +
                        "任务！";

                String key = "autopp_" + user + "_autologs";
                redisTemplate.opsForList().leftPush(key, value);
                redisTemplate.expire(key, 30, TimeUnit.DAYS);
                return;
            }


            // 符合条件的继续执行/////////////////////
            // 任务信息

            TaskDO taskDO = new TaskDO();

            if ("MX".equals(doParamUpper)) {
                taskDO.setType(from + "->" + x);
                taskDO.setGroup(1);
            } else if (CharSequenceUtil.equalsAny("AX", "BX")) {
                taskDO.setType(accountF.getId() + "->" + x);
                taskDO.setGroup(from.intValue());
            }
            taskDO.setMoney(String.valueOf(1));

            taskDO.setRemark("[1] 组 -> 分配自动" + doParamUpper + "任务 " + LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
            taskService.save(taskDO);

            autoInfoDTO.setTaskId(taskDO.getId());

            // 写入日志
            msg = "[" + taskDO.getGroup() + "] 组 -> 任务 => [" + taskDO.getType() + "] =>  金额：[" + taskDO.getMoney() + "] -> Auto" + doParamUpper + "添加完成！";
            autoService.setAutoLog(autoInfoDTO, msg);

            // 增量计数
            v = v + 1;
        }

        autoInfoDTO.setId(0L);
        autoInfoDTO.setGroup(0);
        autoInfoDTO.setTaskId(0L);

        msg = "============== Auto" + doParamUpper + "任务完成！ 新增任务：[ " + v + " ] ==============";
        autoService.setAutoLog(autoInfoDTO, msg);
    }


    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Operation(summary = "查看自动日志 logs()")
    @GetMapping("/logs")
    public List<String> logs() {
        int user = 25;
        String key = "autopp_" + user + "_autologs";
        return redisTemplate.opsForList().range(key, 0, -1);
    }


    private boolean diffTime(LocalDateTime time, int diff) {
        Duration between = LocalDateTimeUtil.between(time, LocalDateTimeUtil.now());
        return between.getSeconds() > diff;
    }

    private Long getX() {
        QueryWrapper<AccountDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().lt(AccountDO::getGroup, 3)
                .eq(AccountDO::getTitle, "X")
                .eq(AccountDO::getStatus, 0)
                .last("ORDER BY RAND() ASC LIMIT 1");
        AccountDO account = accountService.getOne(queryWrapper);
        if (account == null) {
            return 0L;
        }
        account.setStatus(1);
        account.setUpdateTime(LocalDateTimeUtil.now());
        accountService.updateById(account);
        return account.getId();
    }

    @GetMapping("/index")
    public AccountVO index() {
        String key = "autopp_25_taskqueue";
        String task = redisTemplate.opsForList().rightPop(key); // 读取队列

        if (StrUtil.isBlank(task)) {
            throw new BizException("错误：队列空！");
        }


        TaskDO taskDO = taskService.getOne(new QueryWrapper<TaskDO>().lambda().eq(TaskDO::getStatus, 0)
                .eq(TaskDO::getId, task));

        if (taskDO == null) {
            throw new BizException("错误：没有需要执行的任务[1]！");
        } else {

            // 先改状态
            taskService.updateById(new TaskDO().setId(taskDO.getId()).setStatus(1)); // 读取后更新状态为1
        }

        String type = taskDO.getType().trim(); // 去除空格

        AccountDO fromAccount = null;
        AccountDO toAccount = null;


        if (type.length() == 2) {
            String from = type.substring(0, 1); // 取值 第一
            String to = type.substring(1); // 取值 最后

            fromAccount = accountService.getOne(new QueryWrapper<AccountDO>().lambda()
                    .eq(AccountDO::getGroup, taskDO.getGroup())
                    .eq(AccountDO::getTitle, from)
                    .select(AccountDO.class, info -> !info.getColumn().equals("lastlog") &&
                            !info.getColumn().equals("remarks") && !info.getColumn().equals("create_time") &&
                            !info.getColumn().equals("update_time") && !info.getColumn().equals("delete_time")));

            toAccount = accountService.getOne(new QueryWrapper<AccountDO>().lambda()
                    .eq(AccountDO::getGroup, taskDO.getGroup())
                    .eq(AccountDO::getTitle, to)
                    .select(AccountDO.class, info -> !info.getColumn().equals("lastlog") &&
                            !info.getColumn().equals("remarks") && !info.getColumn().equals("create_time") &&
                            !info.getColumn().equals("update_time") && !info.getColumn().equals("delete_time")));

        } else {
            String[] arr = type.split("->");

            String from = arr[0]; // 取值
            String to = arr[1]; // 取值

            fromAccount = accountService.getById(from);

            toAccount = accountService.getById(to);
        }

        if (fromAccount == null) {
            // taskModel.setError(1);
            // taskModel.setInfo("错误：From_Account 不存在！");
            taskService.updateById(new TaskDO().setStatus(3).setId(taskDO.getId())); // 读取后更新状态为3错误

            // Task tk = new Task();
            // tk.setLog(taskModel.getId(), taskModel.getInfo());
            taskService.uploadLog(taskDO.getId(), "错误：From_Account 不存在！");
            throw new BizException("错误：From_Account 不存在！");
        }

        if (toAccount == null) {
            taskService.updateById(new TaskDO().setStatus(3).setId(taskDO.getId())); // 读取后更新状态为3错误

            // Task tk = new Task();
            // tk.setLog(taskModel.getId(), taskModel.getInfo());

            taskService.uploadLog(taskDO.getId(), "错误：To_Account 不存在！");
            throw new BizException("错误：To_Account 不存在！");
        }

        AccountVO accountVO = new AccountVO();
        accountVO.setFromAccount(fromAccount);
        accountVO.setToAccount(toAccount);
        taskService.updateById(new TaskDO().setStatus(1).setId(taskDO.getId())); // 读取后更新状态为1

        return accountVO;
    }


}
