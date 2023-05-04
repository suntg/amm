package com.example.amm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.amm.domain.entity.LogDO;
import com.example.amm.mapper.LogMapper;
import com.example.amm.service.LogService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, LogDO> implements LogService {

    @Autowired
    private LogMapper logMapper;

    @Override
    public void addLog(LogDO logDO) {
        logMapper.insert(logDO);
    }

    @Override
    public void deleteLogById(Long id) {
        logMapper.deleteById(id);
    }

    @Override
    public void updateLog(LogDO logDO) {
        logMapper.updateById(logDO);
    }

    @Override
    public List<LogDO> getLogList() {
        return logMapper.selectList(null);
    }
}
