package com.example.amm.controller;

import java.util.*;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.amm.constant.BusinessType;
import com.example.amm.domain.entity.LogDO;
import com.example.amm.service.AccountService;
import com.example.amm.service.LogService;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.LocalDateTimeUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Log")
@Slf4j
@RestController
@RequestMapping("/log")
public class LogController {

    @Resource
    private LogService logService;
    @Resource
    private AccountService accountService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/writeDb")
    public void writeDb() throws Exception {
        // pp25_accountlog_*
        // pp25_grouplog_*
        // pp25_tasklog_*
        // autopp_accountlog_*
        // autopp_grouplog_*
        // autopp_tasklog_*
        // autopp_25_autologs

        /*String url = "http://w2.ad123.win:58025/pp25/account/lookall";
        WebClient webClient = new WebClient();
        HtmlPage page = webClient.getPage(url);
        List<Object> rows = page.getByXPath("//table[1]/tbody/tr");
        
        List<AccountDO> accountDOList = new ArrayList<>();
        for (Object row : rows) {
        
            List<HtmlTableCell> cells = ((HtmlTableRow)row).getCells();
            AccountDO accountDO = new AccountDO();
            accountDO.setId(Long.valueOf(cells.get(0).asNormalizedText()));
            accountDO.setEmail(cells.get(1).asNormalizedText());
            accountDO.setPayme(cells.get(2).asNormalizedText());
            accountDO.setGroup(Integer.valueOf(cells.get(3).asNormalizedText()));
            accountDO.setTitle(cells.get(4).asNormalizedText());
            accountDO.setBalance(cells.get(5).asNormalizedText());
            accountDO.setUpdateTime(
                LocalDateTimeUtil.of(new DateTime(cells.get(7).asNormalizedText(), DatePattern.NORM_DATETIME_FORMAT)));
            accountDO.setSuccTimes(Integer.parseInt(cells.get(8).asNormalizedText()));
            accountDO.setSuccMoney(cells.get(9).asNormalizedText());
            accountDO.setRecentLog(cells.get(10).asNormalizedText());
            accountDO.setStatus(Integer.parseInt(cells.get(11).asNormalizedText()));
            accountDO.setSwitchIp(Integer.parseInt(cells.get(12).asNormalizedText()));
            accountDO.setPackageNum(Integer.parseInt(cells.get(13).asNormalizedText()));
        
            accountDO.setAffiliation(cells.get(17).asNormalizedText());
            accountDO.setBatch(cells.get(18).asNormalizedText());
            accountDO.setFirstTime(
                LocalDateTimeUtil.of(new DateTime(cells.get(19).asNormalizedText(), DatePattern.NORM_DATETIME_FORMAT)));
            accountDO.setRemark(cells.get(20).asNormalizedText());
            accountDOList.add(accountDO);
        }
        
        accountService.saveBatch(accountDOList);
        
        Thread.sleep(2000);*/

        Map<String, String> map = new HashMap<>();
        map.put("pp25_accountlog_*", BusinessType.ACCOUNT.name());
        map.put("pp25_grouplog_*", BusinessType.GROUP.name());
        map.put("pp25_tasklog_*", BusinessType.TASK.name());
        map.put("autopp_accountlog_*", BusinessType.AUTO_ACCOUNT.name());
        map.put("autopp_grouplog_*", BusinessType.AUTO_GROUP.name());
        map.put("autopp_tasklog_*", BusinessType.AUTO_TASK.name());
        map.put("autopp_25_autologs", BusinessType.AUTO_USER.name());

        List<LogDO> logDOList = new ArrayList<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            Set<String> keys = redisTemplate.keys(key);
            for (String rdsKey : keys) {
                List<Object> rdsValues = redisTemplate.boundListOps(rdsKey).range(0, -1);
                System.out.println(rdsValues);

                /*for (String rdsValue : rdsValues) {
                    int firstIndex = rdsValue.indexOf("[");
                    int secondIndex = rdsValue.indexOf("]");
                    String logTimeStr = rdsValue.substring(firstIndex + 1, secondIndex);
                    System.out.println(logTimeStr);

                    LogDO logDO = new LogDO();
                    logDO.setBusiness(value);
                    if ("autopp_25_autologs".equals(rdsKey)) {
                        logDO.setBusinessId("25");
                    } else {
                        String[] arr = rdsKey.split("_");
                        String lastElement = arr[arr.length - 1];

                        logDO.setBusinessId(lastElement);
                    }

                    logDO.setMessage(rdsValue);
                    logDO.setLogTime(LocalDateTimeUtil.of(new DateTime(logTimeStr, DatePattern.NORM_DATETIME_FORMAT)));

                    logDOList.add(logDO);

                }*/

            }
        }
        // System.out.println(JSONUtil.toJsonStr(logDOList));
        logService.saveBatch(logDOList);
    }
}
