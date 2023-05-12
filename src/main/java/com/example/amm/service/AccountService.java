package com.example.amm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.amm.domain.entity.AccountDO;
import com.example.amm.domain.query.AccountPageQuery;
import com.example.amm.domain.query.PageQuery;
import com.example.amm.domain.vo.AccountVO;

public interface AccountService extends IService<AccountDO> {

    Page<AccountDO> page(PageQuery pageQuery, AccountPageQuery accountPageQuery);

    AccountDO getById(Long id);

    boolean deleteById(Long id);

    boolean saveAccount(AccountDO account);

    boolean updateAccountById(Long id, AccountDO account);

    boolean updateMoneyAndTimesById(Long id, AccountDO account);

    int insertSelective(AccountDO account);

    int updateByPrimaryKeySelective(AccountDO account);

    int updateByPrimaryKey(AccountDO account);

    void uploadLog(Long id, String logs);

    int nextGroup(int group);

    Long nextAccount(Long id);

    AccountVO getAccount(Long id);

    int getNewGroup();

    void syncFirstTime();

}
