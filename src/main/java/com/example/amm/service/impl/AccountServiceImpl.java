package com.example.amm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.amm.domain.entity.AccountDO;
import com.example.amm.mapper.AccountMapper;
import com.example.amm.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountDO> implements AccountService {

    @Resource
    private AccountMapper accountMapper;

    @Override
    public void csvImport(List list) {

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
