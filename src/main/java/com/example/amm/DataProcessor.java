package com.example.amm;

import static com.example.amm.util.DateTimeUtil.diffTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.amm.domain.dto.AutoInfoDTO;
import com.example.amm.domain.entity.AccountDO;
import com.example.amm.domain.entity.TaskDO;
import com.example.amm.service.AccountService;
import com.example.amm.service.AutoService;
import com.example.amm.service.TaskService;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;

public class DataProcessor {
    private final List<Integer> ids; // 存放所有需要查询的 id
    private final int batchSize; // 每批次查询的数量
    private final CountDownLatch latch; // 用于控制并发的计数器
    private final AtomicInteger counter; // 记录存在的数据数量

    private final AutoService autoService;

    private final TaskService taskService;

    private final AccountService accountService;

    private final AutoInfoDTO autoInfoDTO;

    public DataProcessor(List<Integer> ids, int batchSize, AutoInfoDTO autoInfoDTO, AutoService autoService,
        TaskService taskService, AccountService accountService) {
        this.ids = ids;
        this.batchSize = batchSize;
        this.latch = new CountDownLatch(getBatchCount());
        this.counter = new AtomicInteger(0);
        this.autoInfoDTO = autoInfoDTO;
        this.taskService = taskService;
        this.autoService = autoService;
        this.accountService = accountService;
    }

    public int process() throws InterruptedException {
        for (int i = 0; i < getBatchCount(); i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, ids.size());
            List<Integer> batchIds = ids.subList(start, end);

            // 处理每个批次的查询任务
            Thread thread = new Thread(() -> {
                for (Integer group : batchIds) {

                    autoInfoDTO.setGroup(group);
                    // 发送号->有钱号
                    if (accountService
                        .count(new QueryWrapper<AccountDO>().lambda().eq(AccountDO::getGroup, group)) < 3) {
                        continue;
                    }

                    AccountDO accountF = accountService.getOne(new QueryWrapper<AccountDO>().lambda()
                        .eq(AccountDO::getGroup, group).orderByDesc(AccountDO::getBalance).last(" LIMIT 1 "));
                    if (accountF == null) {
                        continue;
                    }

                    int ht = 24;

                    autoInfoDTO.setId(accountF.getId());
                    // 进行时间差值判断
                    if (!diffTime(accountF.getUpdateTime(), ht * 60 * 60)) {

                        String msg = "[" + autoInfoDTO.getGroup() + "] 组 -> 账号 [" + accountF.getTitle() + "] "
                            + accountF.getEmail() + " 不满足" + ht + "小时条件！上次: " + accountF.getUpdateTime();
                        autoService.setAutoLog(autoInfoDTO, msg);
                        continue;
                    }

                    // 符合条件的继续执行

                    // 接收号->最早更新号
                    // TODO

                    List<AccountDO> accountGroupList = accountService.list(new QueryWrapper<AccountDO>().lambda()
                        .eq(AccountDO::getGroup, group).ne(AccountDO::getTitle, "M").ne(AccountDO::getTitle, "X")
                        .orderByAsc(AccountDO::getTitle).select(AccountDO::getTitle));

                    CircularLinkedList<String> list = new CircularLinkedList<>();
                    for (AccountDO accountDO : accountGroupList) {
                        list.insertWithOrder(accountDO.getTitle());
                    }
                    List<String> s = new ArrayList<>();
                    for (int j = 0; j < list.getSize(); j++) {
                        if (j == list.getSize() - 1) {
                            s.add(list.getNode(j) + "_" + list.getNode(0));
                        } else {
                            s.add(list.getNode(j) + "_" + list.getNode(j + 1));
                        }
                    }
                    String nextTitle = null;
                    for (String s1 : s) {
                        if (s1.startsWith(accountF.getTitle())) {
                            nextTitle = s1.split("_")[1];
                        }
                    }

                    AccountDO accountT = accountService.getOne(new QueryWrapper<AccountDO>().lambda()
                        .eq(AccountDO::getGroup, group).eq(AccountDO::getTitle, nextTitle));
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

                    // int money = Math.round(NumberUtil.sub(accountF.getBalance(),
                    // String.valueOf(randomNumber)).floatValue());
                    // if (money > 12) { // 限定最大
                    // money = 12;
                    // }

                    if (Double.parseDouble(accountF.getBalance()) < 1) {
                        String msg = "[" + group + "] 组 -> 账号 [" + accountF.getTitle() + "] " + accountF.getEmail()
                            + " 金额不足！金额: " + accountF.getBalance();
                        autoService.setAutoLog(autoInfoDTO, msg);
                        continue;
                    }

                    String taskType = accountF.getTitle() + accountT.getTitle();

                    if (taskType.length() != 2) {
                        String msg = "[" + group + "] 组 -> 账号 [" + accountF.getTitle() + "] " + accountF.getEmail()
                            + " Type类型不符！Type: " + taskType;
                        autoService.setAutoLog(autoInfoDTO, msg);
                        continue;
                    }

                    TaskDO taskDO = new TaskDO();
                    taskDO.setType(taskType);
                    taskDO.setGroup(group);
                    taskDO.setMoney(accountF.getBalance());
                    taskDO.setRemark("[" + group + "] 组 -> 自动任务添加 "
                        + LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                    taskService.save(taskDO);

                    String msg =
                        "[" + group + "] 组 -> 任务 => [" + taskType + "] =>  金额：[" + taskDO.getMoney() + "] -> Auto添加完成！";
                    autoInfoDTO.setTaskId(taskDO.getId());
                    autoService.setAutoLog(autoInfoDTO, msg);

                    counter.incrementAndGet();
                }
                latch.countDown();
            });
            thread.start();
        }

        // 等待所有任务完成
        latch.await();

        return counter.get();
    }

    private int getBatchCount() {
        return (int)Math.ceil((double)ids.size() / batchSize);
    }

    private boolean queryDataExists(String id) {
        // 根据 id 查询数据是否存在
        // 返回 true 表示存在，false 表示不存在
        return false;
    }
}