package com.example.amm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.amm.domain.entity.BankTypeDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BankTypeMapper extends BaseMapper<BankTypeDO> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(BankTypeDO record);

    BankTypeDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BankTypeDO record);

    int updateByPrimaryKey(BankTypeDO record);
}