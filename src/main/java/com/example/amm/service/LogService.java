package com.example.amm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.amm.domain.entity.LogDO;

import java.util.List;

public interface LogService extends IService<LogDO> {

    void addLog(LogDO logDO);

    void deleteLogById(Long id);

    void updateLog(LogDO logDO);

    List<LogDO> getLogList();
}
