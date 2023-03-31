package com.example.amm.controller;

import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.amm.common.BizException;
import com.example.amm.domain.entity.AccountDO;
import com.example.amm.domain.query.PageQuery;
import com.example.amm.domain.request.AccountBankCsvRequest;
import com.example.amm.service.AccountService;
import com.example.amm.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStreamReader;
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

    @Operation(summary = "CSV导入")
    @PostMapping("/csvImport")
    public void csvImport(MultipartFile file) {

        List<AccountBankCsvRequest> accountBankCsvRequestList;
        try {
            CsvReader reader = CsvUtil.getReader();
            accountBankCsvRequestList = reader.read(new InputStreamReader(file.getInputStream()), AccountBankCsvRequest.class);
        } catch (IOException e) {
            throw new BizException("CSV文件异常");
        }
        accountService.csvImport(accountBankCsvRequestList);
    }


    @Operation(summary = "分页查询")
    @GetMapping("listPage")
    public Page<AccountDO> listPage(PageQuery pageQuery) {
        return accountService.listPage(pageQuery);
    }

    @GetMapping("getById")
    public AccountDO getById(Long id) {
        return accountService.selectByPrimaryKey(id);
    }

    @PostMapping("deleteById")
    public void deleteById(Long id) {
        accountService.deleteByPrimaryKey(id);
    }

    @PostMapping("insert")
    public void insert(@RequestBody AccountDO record) {
        accountService.insert(record);
    }

    @PostMapping("update")
    public void update(@RequestBody AccountDO record) {
        accountService.updateById(record);
    }


}
