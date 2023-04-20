package com.example.amm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.amm.constant.RedisKeyConstant;
import com.example.amm.domain.entity.AccountDO;
import com.example.amm.domain.query.PageQuery;
import com.example.amm.domain.vo.AccountVO;
import com.example.amm.service.AccountService;
import com.example.amm.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    @Operation(summary = "通过id查询")
    @GetMapping("/{id}")
    public AccountDO getById(@PathVariable Long id) {
        return accountService.getById(id);
    }

    @Operation(summary = "通过id删除")
    @DeleteMapping("/delete/{id}")
    public boolean deleteById(@PathVariable Long id) {
        return accountService.deleteById(id);
    }

    @Operation(summary = "新增Account")
    @PostMapping("/save")
    public boolean saveAccount(@RequestBody AccountDO account) {
        return accountService.saveAccount(account);
    }

    @Operation(summary = "更新Account")
    @PutMapping("/update/{id}")
    public boolean updateAccountById(@PathVariable Long id, @RequestBody AccountDO account) {
        return accountService.updateAccountById(id, account);
    }

    @Operation(summary = "更新money和times")
    @PutMapping("/updateMoneyAndTimes/{id}")
    public boolean updateMoneyAndTimesById(@PathVariable Long id, @RequestBody AccountDO account) {
        return accountService.updateMoneyAndTimesById(id, account);
    }

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Operation(summary = "上传日志")
    @PostMapping("/uploadLog/{id}")
    public void uploadLog(@PathVariable("id") Long id, @RequestBody String log) {
        accountService.uploadLog(id, log);
    }

    @Operation(summary = "获取account log")
    @GetMapping("/accountLog/{id}")
    public List<String> getAccountLogListById(@PathVariable String id) {
        return redisTemplate.opsForList().range(RedisKeyConstant.LOG_ACCOUNT_KEY + id, 0, -1);
    }

    @Operation(summary = "获取group log")
    @GetMapping("/groupLog/{id}")
    public List<String> getGroupLogListById(@PathVariable String id) {
        return redisTemplate.opsForList().range(RedisKeyConstant.LOG_GROUP_KEY + id, 0, -1);
    }

    @Operation(summary = "获取next account")
    @GetMapping("/nextAccount/{id}")
    public Long nextAccount(@PathVariable Long id) {
        return accountService.nextAccount(id);
    }

    @Operation(summary = "获取next group")
    @GetMapping("/nextGroup/{group}")
    public int nextGroup(@PathVariable int group) {
        return accountService.nextGroup(group);
    }


    @Operation(summary = "获取getAccountInfo")
    @GetMapping("/getAccountInfo/{id}")
    public AccountVO getAccountInfo(@PathVariable Long id) {
        return accountService.getAccount(id);
    }







}
