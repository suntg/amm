package com.example.amm.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.amm.domain.entity.LogDO;

public interface LogService extends IService<LogDO> {

    void addLog(LogDO logDO);

    void deleteLogById(Long id);

    void updateLog(LogDO logDO);

    List<LogDO> getLogList();
}
