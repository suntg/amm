package com.example.amm.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.amm.domain.entity.BankDO;

@Deprecated
@Mapper
public interface BankMapper extends BaseMapper<BankDO> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(BankDO record);

    BankDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BankDO record);

    int updateByPrimaryKey(BankDO record);
}