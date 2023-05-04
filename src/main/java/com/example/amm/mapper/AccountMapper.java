package com.example.amm.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.amm.domain.entity.AccountDO;

@Mapper
public interface AccountMapper extends BaseMapper<AccountDO> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(AccountDO record);

    AccountDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AccountDO record);

    int updateByPrimaryKey(AccountDO record);
}