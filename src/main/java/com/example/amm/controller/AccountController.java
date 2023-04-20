package com.example.amm.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.amm.constant.RedisKeyConstant;
import com.example.amm.domain.entity.AccountDO;
import com.example.amm.domain.query.PageQuery;
import com.example.amm.service.AccountService;
import com.example.amm.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Tag(name = "Account")
@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

    @Resource
    private AccountService accountService;

    @Resource
    private BankService bankService;

    // @Operation(summary = "CSV导入")
    // @PostMapping("/csvImport")
    // public void csvImport(MultipartFile file) {
    //
    //     List<AccountBankCsvRequest> accountBankCsvRequestList;
    //     try {
    //         CsvReader reader = CsvUtil.getReader();
    //         accountBankCsvRequestList = reader.read(new InputStreamReader(file.getInputStream()), AccountBankCsvRequest.class);
    //     } catch (IOException e) {
    //         throw new BizException("CSV文件异常");
    //     }
    //     accountService.csvImport(accountBankCsvRequestList);
    // }


    @Operation(summary = "分页查询")
    @GetMapping("listPage")
    public Page<AccountDO> listPage(PageQuery pageQuery) {
        return accountService.listPage(pageQuery);
    }

    @GetMapping("/{id}")
    public AccountDO getById(@PathVariable Long id) {
        return accountService.getById(id);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteById(@PathVariable Long id) {
        return accountService.deleteById(id);
    }

    @PostMapping("/save")
    public boolean saveAccount(@RequestBody AccountDO account) {
        return accountService.saveAccount(account);
    }

    @PutMapping("/update/{id}")
    public boolean updateAccountById(@PathVariable Long id, @RequestBody AccountDO account) {
        return accountService.updateAccountById(id, account);
    }


    @PutMapping("/updateMoneyAndTimes/{id}")
    public boolean updateMoneyAndTimesById(@PathVariable Long id, @RequestBody AccountDO account) {
        return accountService.updateMoneyAndTimesById(id, account);
    }

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/uploadLog/{id}")
    public void uploadLog(@PathVariable("id") String id, @RequestBody String log) {

        StringBuilder value = new StringBuilder();
        value.append("[");
        value.append(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        value.append("]");
        value.append(" => ");
        value.append(log);


        redisTemplate.opsForList().leftPush(RedisKeyConstant.LOG_ACCOUNT_KEY + id, String.valueOf(value));
        redisTemplate.expire(RedisKeyConstant.LOG_ACCOUNT_KEY + id, 90, TimeUnit.DAYS);


        AccountDO accountDO = accountService.getById(id);

        redisTemplate.opsForList().leftPush(RedisKeyConstant.LOG_GROUP_KEY + accountDO.getGroup(), String.valueOf(value));
        redisTemplate.expire(RedisKeyConstant.LOG_GROUP_KEY + accountDO.getGroup(), 90, TimeUnit.DAYS);

    }

    @GetMapping("/accountLog/{id}")
    public List<String> getAccountLogListById(@PathVariable String id) {
        return redisTemplate.opsForList().range(RedisKeyConstant.LOG_ACCOUNT_KEY + id, 0, -1);
    }


    @GetMapping("/groupLog/{id}")
    public List<String> getGroupLogListById(@PathVariable String id) {
        return redisTemplate.opsForList().range(RedisKeyConstant.LOG_GROUP_KEY + id, 0, -1);
    }
}
