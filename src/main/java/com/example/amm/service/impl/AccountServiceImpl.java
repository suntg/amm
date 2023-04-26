package com.example.amm.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.amm.constant.RedisKeyConstant;
import com.example.amm.domain.entity.AccountDO;
import com.example.amm.domain.query.PageQuery;
import com.example.amm.domain.request.AccountBankCsvRequest;
import com.example.amm.domain.vo.AccountVO;
import com.example.amm.mapper.AccountMapper;
import com.example.amm.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountDO> implements AccountService {

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void csvImport(List<AccountBankCsvRequest> list) {
        // for (AccountBankCsvRequest accountBankCsvRequest : list) {
        //     if (StrUtil.isBlank(accountBankCsvRequest.getAccountFileName())) {
        //         throw new BizException("filename为空");
        //     }
        //     AccountDO result = accountMapper.selectOne(new QueryWrapper<AccountDO>().lambda().eq(AccountDO::getFileName, accountBankCsvRequest.getAccountFileName()));
        //     if (result == null) {
        //         AccountDO accountDO = AccountBankCsvConverter.INSTANCE.csv2AccountDO(accountBankCsvRequest);
        //         accountMapper.insert(accountDO);
        //
        //         BankDO bankDO = AccountBankCsvConverter.INSTANCE.csv2BankDO(accountBankCsvRequest);
        //         bankDO.setAccountId(accountDO.getId());
        //         bankService.insert(bankDO);
        //     } else {
        //         BankDO bankDO = AccountBankCsvConverter.INSTANCE.csv2BankDO(accountBankCsvRequest);
        //         bankDO.setAccountId(result.getId());
        //         bankService.insert(bankDO);
        //     }
        //
        // }
    }

    @Override
    public Page<AccountDO> listPage(PageQuery pageQuery) {
        return this.page(new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()), new QueryWrapper<AccountDO>().lambda()
                // .gt(AccountDO::getGroupStatus, 0)
                .orderByDesc(AccountDO::getCreateTime));
    }

    @Override
    public boolean deleteById(Long id) {
        return accountMapper.deleteById(id) > 0;
    }

    @Override
    public boolean saveAccount(AccountDO record) {
        return accountMapper.insert(record) > 0;
    }

    @Override
    public boolean updateAccountById(Long id, AccountDO account) {
        account.setId(id);
        if (account.getChangeTimeFlag() == 0) {
            account.setUpdateTime(LocalDateTimeUtil.now());
        }
        return accountMapper.updateById(account) > 0;
    }

    @Override
    public boolean updateMoneyAndTimesById(Long id, AccountDO account) {
        AccountDO result = accountMapper.selectById(id);
        if (result != null) {
            String money = String.valueOf(NumberUtil.add(account.getSuccMoney(), result.getSuccMoney()));
            int times = result.getSuccTimes();
            result.setSuccTimes(++times);
            LambdaUpdateWrapper<AccountDO> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(AccountDO::getId, id).set(AccountDO::getSuccMoney, money).set(AccountDO::getSuccTimes, times);

            return accountMapper.update(null, lambdaUpdateWrapper) > 0;
        }
        return false;
    }

    @Override
    public int insertSelective(AccountDO account) {
        return accountMapper.insertSelective(account);
    }

    @Override
    public AccountDO getById(Long id) {
        return accountMapper.selectById(id);
    }

    @Override
    public int updateByPrimaryKeySelective(AccountDO account) {
        return accountMapper.updateByPrimaryKeySelective(account);
    }

    @Override
    public int updateByPrimaryKey(AccountDO account) {
        return accountMapper.updateByPrimaryKey(account);
    }

    @Override
    public void uploadLog(Long id, String logs) {

        StringBuilder value = new StringBuilder();
        value.append("[");
        value.append(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        value.append("]");
        value.append(" => ");
        value.append(logs);


        redisTemplate.opsForList().leftPush(RedisKeyConstant.ACCOUNT_LOG_KEY + id, String.valueOf(value));
        redisTemplate.expire(RedisKeyConstant.ACCOUNT_LOG_KEY + id, 30, TimeUnit.DAYS);


        AccountDO accountDO = accountMapper.selectById(id);

        redisTemplate.opsForList().leftPush(RedisKeyConstant.GROUP_LOG_KEY + accountDO.getGroup(), String.valueOf(value));
        redisTemplate.expire(RedisKeyConstant.GROUP_LOG_KEY + accountDO.getGroup(), 90, TimeUnit.DAYS);


        accountDO.setRecentLog(logs);
        accountMapper.updateById(accountDO);
    }

    @Override
    public int nextGroup(int group) {
        AccountDO accountDO = accountMapper.selectOne(new QueryWrapper<AccountDO>().lambda()
                .gt(AccountDO::getGroup, group)
                .orderByAsc(AccountDO::getGroup).last(" LIMIT 1 "));
        if (accountDO == null) {
            return group;
        }
        return accountDO.getGroup();
    }

    @Override
    public Long nextAccount(Long id) {
        AccountDO accountDO = accountMapper.selectById(id);
        AccountDO next = accountMapper.selectOne(new QueryWrapper<AccountDO>().lambda()
                .ne(AccountDO::getGroup, accountDO.getGroup())
                .ge(AccountDO::getId, id)
                .orderByAsc(AccountDO::getId).last(" LIMIT 1 "));
        if (next == null) {
            int nextGroup = nextGroup(accountDO.getGroup());

            next = accountMapper.selectOne(new QueryWrapper<AccountDO>().lambda().ne(AccountDO::getGroup, nextGroup)
                    .orderByAsc(AccountDO::getId).last(" LIMIT 1 "));
        }
        if (next == null) {
            return null;
        }
        return next.getId();
    }

    @Override
    public int getNewGroup() {
        AccountDO account = accountMapper.selectOne(
                new QueryWrapper<AccountDO>().lambda().gt(AccountDO::getGroup, 1)
                        .select(AccountDO::getGroup)
                        .orderByDesc(AccountDO::getGroup)
                        .last("limit 1"));
        if (account == null) {
            return 2;
        } else {
            return account.getGroup() + 1;
        }
    }

    /**
     * 在快捷创建task页面获取account信息
     *
     * @param id
     * @return
     */
    @Override
    public AccountVO getAccount(Long id) {
        AccountDO accountDO = accountMapper.selectById(id);
        List<AccountDO> accountDOList = accountMapper.selectList(new QueryWrapper<AccountDO>().lambda()
                .eq(AccountDO::getGroup, accountDO.getGroup()));

        AccountVO accountVO = new AccountVO();

        accountVO.setCurrentAccount(accountDO);
        accountVO.setAccountDOList(accountDOList);
        int money = Math.round(NumberUtil.sub(accountDO.getBalance(), String.valueOf(RandomUtil.randomInt(1, 3))).floatValue());
        if (money > 15) {  // 限定最大
            money = 15;
        }
        accountVO.setMoney(money);

        return accountVO;
    }

}
