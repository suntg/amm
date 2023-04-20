package com.example.amm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.amm.domain.entity.TaskDO;
import com.example.amm.domain.query.PageQuery;

import java.util.Map;

public interface TaskService extends IService<TaskDO> {


    boolean saveTask(TaskDO taskDO);

    boolean updateTaskById(Long id, TaskDO taskDO);

    boolean removeTaskById(Long id);

    TaskDO getTaskById(Long id);

    Page<TaskDO> listPage(PageQuery pageQuery);


    void createQuickTask(TaskDO task);

}
