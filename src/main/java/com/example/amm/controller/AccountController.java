package com.example.amm.controller;

import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import com.example.amm.common.BizException;
import com.example.amm.domain.request.AccountBankCsvRequest;
import com.example.amm.service.AccountService;
import com.example.amm.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
        accountService.csvImport(null);
    }


}
