package com.example.amm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.amm.domain.entity.AccountDO;
import com.example.amm.domain.query.PageQuery;
import com.example.amm.service.AccountService;
import com.example.amm.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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

}
