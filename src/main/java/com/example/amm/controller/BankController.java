package com.example.amm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.amm.domain.entity.AccountDO;
import com.example.amm.domain.entity.BankDO;
import com.example.amm.domain.query.PageQuery;
import com.example.amm.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "Bank")
@Slf4j
@RestController
@RequestMapping("/bank")
public class BankController {

    @Resource
    private BankService bankService;



    @Operation(summary = "分页查询")
    @GetMapping("listPage")
    public Page<BankDO> listPage(PageQuery pageQuery) {
        return bankService.listPage(pageQuery);
    }

    @GetMapping("getById")
    public BankDO getById(Long id) {
        return bankService.selectByPrimaryKey(id);
    }

    @PostMapping("deleteById")
    public void deleteById(Long id) {
        bankService.deleteByPrimaryKey(id);
    }

    @PostMapping("insert")
    public void insert(BankDO record) {
        bankService.insert(record);
    }

    @PostMapping("update")
    public void update(BankDO record) {
        bankService.updateById(record);
    }


}
