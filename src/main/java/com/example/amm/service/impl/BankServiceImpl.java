package com.example.amm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.amm.domain.entity.BankDO;
import com.example.amm.domain.query.PageQuery;
import com.example.amm.mapper.BankMapper;
import com.example.amm.service.BankService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BankServiceImpl extends ServiceImpl<BankMapper, BankDO> implements BankService {

    @Resource
    private BankMapper bankMapper;

    @Override
    public Page<BankDO> listPage(PageQuery pageQuery) {
        return this.page(new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()), new QueryWrapper<BankDO>()
                .lambda().orderByDesc(BankDO::getCreateTime));
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return bankMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(BankDO record) {
        return bankMapper.insert(record);
    }

    @Override
    public int insertSelective(BankDO record) {
        return bankMapper.insertSelective(record);
    }

    @Override
    public BankDO selectByPrimaryKey(Long id) {
        return bankMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(BankDO record) {
        return bankMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(BankDO record) {
        return bankMapper.updateByPrimaryKey(record);
    }

}
