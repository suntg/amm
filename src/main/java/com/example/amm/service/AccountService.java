package com.example.amm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.amm.domain.entity.AccountDO;

import java.util.List;

public interface AccountService extends IService<AccountDO> {


    void csvImport(List list);


    int deleteByPrimaryKey(Long id);

    int insert(AccountDO record);

    int insertSelective(AccountDO record);

    AccountDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AccountDO record);

    int updateByPrimaryKey(AccountDO record);

}
