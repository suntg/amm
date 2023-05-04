package com.example.amm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.amm.domain.entity.BankTypeDO;

@Deprecated
public interface BankTypeService extends IService<BankTypeDO> {

    int deleteByPrimaryKey(Long id);

    int insert(BankTypeDO record);

    int insertSelective(BankTypeDO record);

    BankTypeDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BankTypeDO record);

    int updateByPrimaryKey(BankTypeDO record);

}
