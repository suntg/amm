package com.example.amm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.amm.common.BizException;
import com.example.amm.converter.AccountBankCsvConverter;
import com.example.amm.domain.entity.AccountDO;
import com.example.amm.domain.entity.BankDO;
import com.example.amm.domain.query.PageQuery;
import com.example.amm.domain.request.AccountBankCsvRequest;
import com.example.amm.mapper.AccountMapper;
import com.example.amm.service.AccountService;
import com.example.amm.service.BankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountDO> implements AccountService {

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private BankService bankService;

    @Override
    public void csvImport(List<AccountBankCsvRequest> list) {
        for (AccountBankCsvRequest accountBankCsvRequest : list) {
            if (StrUtil.isBlank(accountBankCsvRequest.getAccountFileName())) {
                throw new BizException("filename为空");
            }
            AccountDO result = accountMapper.selectOne(new QueryWrapper<AccountDO>().lambda().eq(AccountDO::getFileName, accountBankCsvRequest.getAccountFileName()));
            if (result == null) {
                AccountDO accountDO = AccountBankCsvConverter.INSTANCE.csv2AccountDO(accountBankCsvRequest);
                accountMapper.insert(accountDO);

                BankDO bankDO = AccountBankCsvConverter.INSTANCE.csv2BankDO(accountBankCsvRequest);
                bankDO.setAccountId(accountDO.getId());
                bankService.insert(bankDO);
            } else {
                BankDO bankDO = AccountBankCsvConverter.INSTANCE.csv2BankDO(accountBankCsvRequest);
                bankDO.setAccountId(result.getId());
                bankService.insert(bankDO);
            }

        }
    }

    @Override
    public Page<AccountDO> listPage(PageQuery pageQuery) {
        return this.page(new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()), new QueryWrapper<AccountDO>()
                .lambda().orderByDesc(AccountDO::getCreateTime));
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return accountMapper.deleteById(id);
    }

    @Override
    public int insert(AccountDO record) {
        return accountMapper.insert(record);
    }

    @Override
    public int insertSelective(AccountDO record) {
        return accountMapper.insertSelective(record);
    }

    @Override
    public AccountDO selectByPrimaryKey(Long id) {
        return accountMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(AccountDO record) {
        return accountMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(AccountDO record) {
        return accountMapper.updateByPrimaryKey(record);
    }

}
