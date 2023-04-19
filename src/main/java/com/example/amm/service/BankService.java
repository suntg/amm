package com.example.amm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.amm.domain.entity.BankDO;
import com.example.amm.domain.query.PageQuery;

public interface BankService extends IService<BankDO> {

    Page<BankDO> listPage(PageQuery pageQuery);

    int deleteByPrimaryKey(Long id);

    int insert(BankDO record);

    int insertSelective(BankDO record);

    BankDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BankDO record);

    int updateByPrimaryKey(BankDO record);

}
