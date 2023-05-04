package com.example.amm.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.amm.domain.entity.BankTypeDO;
import com.example.amm.mapper.BankTypeMapper;
import com.example.amm.service.BankTypeService;

@Deprecated
@Service
public class BankTypeServiceImpl extends ServiceImpl<BankTypeMapper, BankTypeDO> implements BankTypeService {

    @Resource
    private BankTypeMapper bankTypeMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return bankTypeMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(BankTypeDO record) {
        return bankTypeMapper.insert(record);
    }

    @Override
    public int insertSelective(BankTypeDO record) {
        return bankTypeMapper.insertSelective(record);
    }

    @Override
    public BankTypeDO selectByPrimaryKey(Long id) {
        return bankTypeMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(BankTypeDO record) {
        return bankTypeMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(BankTypeDO record) {
        return bankTypeMapper.updateByPrimaryKey(record);
    }

}
