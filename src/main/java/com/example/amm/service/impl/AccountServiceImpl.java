package com.example.amm.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.amm.constant.BusinessType;
import com.example.amm.domain.entity.AccountDO;
import com.example.amm.domain.entity.LogDO;
import com.example.amm.domain.query.AccountPageQuery;
import com.example.amm.domain.query.PageQuery;
import com.example.amm.domain.vo.AccountVO;
import com.example.amm.mapper.AccountMapper;
import com.example.amm.service.AccountService;
import com.example.amm.service.LogService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountDO> implements AccountService {

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private LogService logService;

    @Override
    public Page<AccountDO> page(PageQuery pageQuery, AccountPageQuery accountPageQuery) {
        // 查询
        LambdaQueryWrapper<AccountDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeLeft(CharSequenceUtil.isNotBlank(accountPageQuery.getEmail()), AccountDO::getEmail,
            accountPageQuery.getEmail());
        List<String> groups = new ArrayList<>();
        if (CharSequenceUtil.isNotBlank(accountPageQuery.getGroup())) {
            groups = Arrays.asList(accountPageQuery.getGroup().split(","));
        }
        wrapper.in(CollUtil.isNotEmpty(groups), AccountDO::getGroup, groups);
        wrapper.groupBy(AccountDO::getGroup);

        if (!"M".equals(accountPageQuery.getTitle()) && !"X".equals(accountPageQuery.getTitle())) {
            // 既不等于 M 也不等于 X
            wrapper.ne(AccountDO::getTitle, "M").ne(AccountDO::getTitle, "X");
        }
        if ("M".equals(accountPageQuery.getTitle())) {
            wrapper.eq(AccountDO::getTitle, "M");
        }
        if ("X".equals(accountPageQuery.getTitle())) {
            wrapper.eq(AccountDO::getTitle, "X");
        }

        wrapper.gt(AccountDO::getGroupStatus, 0).orderByAsc(AccountDO::getGroup).orderByAsc(AccountDO::getTitle);

        if (accountPageQuery.getIntervalDay() != null) {
            if (CharSequenceUtil.isNotBlank(accountPageQuery.getTitle())) {
                LocalDateTime offset = LocalDateTimeUtil.offset(LocalDateTimeUtil.now(),
                    -accountPageQuery.getIntervalDay(), ChronoUnit.DAYS);
                wrapper.having("MAX(first_time <= '" + offset.toString() + "' AND title = 'C') > 0 AND"
                    + " SUM(CASE WHEN title = '" + accountPageQuery.getTitle() + "' THEN 1 ELSE 0 END) > 0 ");
            } else {
                LocalDateTime offset = LocalDateTimeUtil.offset(LocalDateTimeUtil.now(),
                    -accountPageQuery.getIntervalDay(), ChronoUnit.DAYS);
                wrapper.having("MAX(first_time <= {0} AND title = 'C') > 0 ", offset.toString());
            }
        } else {
            wrapper.eq(CharSequenceUtil.isNotBlank(accountPageQuery.getTitle()), AccountDO::getTitle,
                accountPageQuery.getTitle());
        }

        // 查询group
        wrapper.select(AccountDO::getGroup);
        List<AccountDO> groupList = accountMapper.selectList(wrapper);

        // group分组后，进行in查询
        Page<AccountDO> page = new Page<>();
        page.setSize(pageQuery.getPageSize());
        if (CollUtil.isNotEmpty(groupList)) {
            // 通过对group分组内存分组
            List<AccountDO> groupPage = CollUtil.page(pageQuery.getPageNum() - 1, pageQuery.getPageSize(), groupList);
            List<AccountDO> accountDOList = accountMapper.selectList(new QueryWrapper<AccountDO>().lambda()
                .eq(CharSequenceUtil.isNotBlank(accountPageQuery.getTitle()), AccountDO::getTitle,
                    accountPageQuery.getTitle())
                .in(AccountDO::getGroup, groupPage.stream().map(AccountDO::getGroup).collect(Collectors.toList()))
                .orderByAsc(AccountDO::getGroup).orderByAsc(AccountDO::getTitle));
            page.setTotal(groupList.size());
            page.setRecords(accountDOList);
        }
        return page;
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
        if (account.getChangeTimeFlag() == 1) {
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
            lambdaUpdateWrapper.eq(AccountDO::getId, id).set(AccountDO::getSuccMoney, money)
                .set(AccountDO::getSuccTimes, times).set(AccountDO::getUpdateTime, LocalDateTimeUtil.now());
            if (result.getFirstTime() == null) {
                lambdaUpdateWrapper.set(AccountDO::getFirstTime, LocalDateTimeUtil.now());
            }
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
    @Transactional(rollbackFor = Exception.class)
    public void uploadLog(Long id, String logs) {

        String value = "[" + LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN) + "]"
            + " => " + logs;

        LogDO logDO = new LogDO();
        logDO.setBusinessId(String.valueOf(id));
        logDO.setMessage(value);
        logDO.setBusiness(BusinessType.ACCOUNT.toString());
        logDO.setLogTime(LocalDateTimeUtil.now());
        logService.save(logDO);

        // TODO
        // redisTemplate.opsForList().leftPush(RedisKeyConstant.ACCOUNT_LOG_KEY + id, value);
        // redisTemplate.expire(RedisKeyConstant.ACCOUNT_LOG_KEY + id, 30, TimeUnit.DAYS);

        AccountDO accountDO = accountMapper.selectById(id);

        logDO.setId(null);
        logDO.setBusinessId(String.valueOf(accountDO.getGroup()));
        logDO.setBusiness(BusinessType.GROUP.toString());
        logService.save(logDO);

        // TODO
        // redisTemplate.opsForList().leftPush(RedisKeyConstant.GROUP_LOG_KEY + accountDO.getGroup(),
        // String.valueOf(value));
        // redisTemplate.expire(RedisKeyConstant.GROUP_LOG_KEY + accountDO.getGroup(), 90, TimeUnit.DAYS);

        accountDO.setRecentLog(logs);
        accountMapper.updateById(accountDO);
    }

    @Override
    public int nextGroup(int group) {
        AccountDO accountDO = accountMapper.selectOne(new QueryWrapper<AccountDO>().lambda()
            .gt(AccountDO::getGroup, group).orderByAsc(AccountDO::getGroup).last(" LIMIT 1 "));
        if (accountDO == null) {
            return group;
        }
        return accountDO.getGroup();
    }

    @Override
    public Long nextAccount(Long id) {
        AccountDO accountDO = accountMapper.selectById(id);
        AccountDO next =
            accountMapper.selectOne(new QueryWrapper<AccountDO>().lambda().ne(AccountDO::getGroup, accountDO.getGroup())
                .ge(AccountDO::getId, id).orderByAsc(AccountDO::getId).last(" LIMIT 1 "));
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
        AccountDO account = accountMapper.selectOne(new QueryWrapper<AccountDO>().lambda().gt(AccountDO::getGroup, 1)
            .select(AccountDO::getGroup).orderByDesc(AccountDO::getGroup).last("limit 1"));
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
        List<AccountDO> accountDOList = accountMapper
            .selectList(new QueryWrapper<AccountDO>().lambda().eq(AccountDO::getGroup, accountDO.getGroup()));

        AccountVO accountVO = new AccountVO();

        accountVO.setCurrentAccount(accountDO);
        accountVO.setAccountDOList(accountDOList);
        int money =
            Math.round(NumberUtil.sub(accountDO.getBalance(), String.valueOf(RandomUtil.randomInt(1, 3))).floatValue());
        if (money > 15) { // 限定最大
            money = 15;
        }
        accountVO.setMoney(money);

        return accountVO;
    }

}
