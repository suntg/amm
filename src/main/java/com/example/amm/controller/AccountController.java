package com.example.amm.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.amm.common.BizException;
import com.example.amm.constant.BusinessType;
import com.example.amm.domain.entity.AccountDO;
import com.example.amm.domain.entity.LogDO;
import com.example.amm.domain.query.AccountPageQuery;
import com.example.amm.domain.query.PageQuery;
import com.example.amm.domain.vo.AccountVO;
import com.example.amm.service.AccountService;
import com.example.amm.service.LogService;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Account")
@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

    @Resource
    private AccountService accountService;

    @Resource
    private LogService logService;

    @Operation(summary = "分页查询")
    @GetMapping("page")
    public Page<AccountDO> page(PageQuery pageQuery, AccountPageQuery accountPageQuery) {
        return accountService.page(pageQuery, accountPageQuery);
    }

    @Operation(summary = "list")
    @GetMapping("/list")
    public List<AccountDO> list() {
        return accountService.list(
            new QueryWrapper<AccountDO>().lambda().orderByAsc(AccountDO::getGroup).orderByAsc(AccountDO::getTitle));
    }

    @Operation(summary = "listNeMX")
    @GetMapping("/listNeMX")
    public List<AccountDO> listNeMX() {
        return accountService
            .list(new QueryWrapper<AccountDO>().lambda().ne(AccountDO::getTitle, "M").ne(AccountDO::getTitle, "X")
                .gt(AccountDO::getGroupStatus, 0).orderByAsc(AccountDO::getGroup).orderByAsc(AccountDO::getTitle));
    }

    @Operation(summary = "listByPage")
    @GetMapping("/listByPage")
    public Page<AccountDO> listByPage(PageQuery pageQuery) {
        LambdaQueryWrapper<AccountDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(AccountDO::getGroup).groupBy(AccountDO::getGroup).orderByAsc(AccountDO::getGroup);

        Page<AccountDO> accountDOPage =
            accountService.page(new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()), wrapper);

        List<AccountDO> accountDOList = accountService.list(new QueryWrapper<AccountDO>().lambda()
            .in(AccountDO::getGroup,
                accountDOPage.getRecords().stream().map(AccountDO::getGroup).collect(Collectors.toList()))
            .orderByAsc(AccountDO::getGroup).orderByAsc(AccountDO::getTitle));

        accountDOPage.setRecords(accountDOList);
        return accountDOPage;
    }

    @Operation(summary = "listNeMXByPage")
    @GetMapping("/listNeMXByPage")
    public Page<AccountDO> listNeMXByPage(PageQuery pageQuery) {
        LambdaQueryWrapper<AccountDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(AccountDO::getGroup).ne(AccountDO::getTitle, "M").ne(AccountDO::getTitle, "X")
            .gt(AccountDO::getGroupStatus, 0).groupBy(AccountDO::getGroup).orderByAsc(AccountDO::getGroup);

        Page<AccountDO> accountDOPage =
            accountService.page(new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()), wrapper);

        List<AccountDO> accountDOList = accountService.list(new QueryWrapper<AccountDO>().lambda()
            .in(AccountDO::getGroup,
                accountDOPage.getRecords().stream().map(AccountDO::getGroup).collect(Collectors.toList()))
            .orderByAsc(AccountDO::getGroup).orderByAsc(AccountDO::getTitle));

        accountDOPage.setRecords(accountDOList);
        return accountDOPage;
    }

    @Operation(summary = "通过id查询 edit($id)")
    @GetMapping("/{id}")
    public AccountDO getById(@PathVariable Long id) {
        return accountService.getById(id);
    }

    @Operation(summary = "通过id删除 del($id)")
    @DeleteMapping("/delete/{id}")
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(@PathVariable Long id) {
        accountService.update(new UpdateWrapper<AccountDO>().lambda().eq(AccountDO::getId, id)
            .set(AccountDO::getDeleteTime, LocalDateTimeUtil.now()));
        return accountService.deleteById(id);
    }

    @Operation(summary = "新增Account add() creat()")
    @PostMapping("/save")
    public boolean saveAccount(@RequestBody AccountDO account) {
        return accountService.saveAccount(account);
    }

    @Operation(summary = "更新Account update() setbalance($id,$balance) setpath($id,$path) remarks($id,$remarks)")
    @PutMapping("/update/{id}")
    public boolean updateAccountById(@PathVariable Long id, @RequestBody AccountDO account) {
        return accountService.updateAccountById(id, account);
    }

    @Operation(summary = "更新money和times succ_report($id,$money)")
    @PutMapping("/updateMoneyAndTimes/{id}")
    public boolean updateMoneyAndTimesById(@PathVariable Long id, @RequestBody AccountDO account) {
        return accountService.updateMoneyAndTimesById(id, account);
    }

    @Operation(summary = "上传日志 setlog($id,$value)")
    @PostMapping("/uploadLog/{id}")
    public void uploadLog(@PathVariable("id") Long id, @RequestBody String log) {
        accountService.uploadLog(id, log);
    }

    @Operation(summary = "获取account log viewlog($id)")
    @GetMapping("/accountLog/{id}")
    public List<String> getAccountLogListById(@PathVariable String id) {
        // redisTemplate.opsForList().range(RedisKeyConstant.ACCOUNT_LOG_KEY + id, 0, -1);
        return logService.list(new QueryWrapper<LogDO>().lambda().eq(LogDO::getBusinessId, id)
            .eq(LogDO::getBusiness, BusinessType.ACCOUNT.toString()).orderByDesc(LogDO::getLogTime)
            .orderByDesc(LogDO::getId)).stream().map(LogDO::getMessage).collect(Collectors.toList());
    }

    @Operation(summary = "获取group log grouplog($id)")
    @GetMapping("/groupLog/{id}")
    public List<String> getGroupLogListById(@PathVariable String id) {
        // redisTemplate.opsForList().range(RedisKeyConstant.GROUP_LOG_KEY + id, 0, -1);

        return logService.list(new QueryWrapper<LogDO>().lambda().eq(LogDO::getBusinessId, id)
            .eq(LogDO::getBusiness, BusinessType.GROUP.toString()).orderByDesc(LogDO::getLogTime)
            .orderByDesc(LogDO::getId)).stream().map(LogDO::getMessage).collect(Collectors.toList());

    }

    @Operation(summary = "获取next account")
    @GetMapping("/nextAccount/{id}")
    public Long nextAccount(@PathVariable Long id) {
        return accountService.nextAccount(id);
    }

    /*@Operation(summary = "获取next group")
    @GetMapping("/nextGroup/{group}")
    public int nextGroup(@PathVariable int group) {
        return accountService.nextGroup(group);
    }*/

    @Operation(summary = "获取getAccountInfo  edit($id)")
    @GetMapping("/getAccountInfo/{id}")
    public AccountVO getAccountInfo(@PathVariable Long id) {
        Long nextId = accountService.nextAccount(id);
        if (nextId == null) {
            throw new BizException("没有数据了");
        }

        AccountVO accountVO = new AccountVO();
        accountVO.setNextId(nextId);
        accountVO.setCurrentAccount(accountService.getById(id));
        return accountVO;
    }

    @Operation(summary = "获取getAccountInfo  qtask($id)")
    @GetMapping("/quickTaskInfo/{id}")
    public AccountVO quickTaskInfo(@PathVariable Long id) {
        return accountService.getAccount(id);
    }

    @Operation(summary = "导入CSV extosql()")
    @PostMapping("/importCsv")
    public void importCsv(@RequestParam("file") MultipartFile file) {
        try {
            CsvReader reader = CsvUtil.getReader();
            List<AccountDO> accountDOList = reader.read(new InputStreamReader(file.getInputStream()), AccountDO.class);
            for (AccountDO accountDO : accountDOList) {
                accountDO.setDeleted(0);
                accountDO.setStatus(1);
                accountDO.setUpdateTime(LocalDateTimeUtil.now());
            }
            accountService.saveBatch(accountDOList);
        } catch (IOException e) {
            throw new BizException("CSV文件异常");
        }
    }

    @Operation(summary = "get_new_group()")
    @GetMapping("/getNewGroup")
    public int getNewGroup() {
        return accountService.getNewGroup();
    }

    @GetMapping("/groupStart/{id}")
    public void groupStart(@PathVariable("id") Long id) {
        accountService.update(new UpdateWrapper<AccountDO>().lambda().eq(AccountDO::getGroup, id)
            .set(AccountDO::getGroupStatus, 1).set(AccountDO::getUpdateTime, LocalDateTimeUtil.now()));

    }

    @GetMapping("/groupStop/{id}")
    public void groupStop(@PathVariable("id") Long id) {
        accountService.update(new UpdateWrapper<AccountDO>().lambda().eq(AccountDO::getGroup, id)
            .set(AccountDO::getGroupStatus, 0).set(AccountDO::getUpdateTime, LocalDateTimeUtil.now()));
    }

    @Operation(summary = "查询所有title")
    @GetMapping("/findDistinctTitles")
    public List<String> findDistinctTitles() {
        QueryWrapper<AccountDO> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT title").orderByAsc("title");
        List<Map<String, Object>> maps = accountService.listMaps(wrapper);
        return maps.stream().map(map -> (String)map.get("title")).collect(Collectors.toList());
    }

    @Operation(summary = "批量更新余额")
    @PutMapping("/batchUpdateBalanceByIds")
    public boolean batchUpdateBalanceByIds(@RequestParam("ids") List<Long> ids,
        @RequestParam("balance") String balance) {
        LambdaUpdateWrapper<AccountDO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(AccountDO::getId, ids);
        wrapper.set(AccountDO::getBalance, balance);
        return accountService.update(wrapper);
    }

    @Operation(summary = "通过id集合查询path")
    @GetMapping("/queryPathsByIds")
    public List<AccountDO> queryPathsByIds(@RequestParam List<Long> ids) {
        LambdaQueryWrapper<AccountDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(AccountDO::getPath, AccountDO::getId).in(AccountDO::getId, ids);
        return accountService.list(wrapper);
    }

}
