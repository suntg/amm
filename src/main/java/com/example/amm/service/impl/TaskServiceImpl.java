package com.example.amm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.amm.domain.entity.TaskDO;
import com.example.amm.domain.query.PageQuery;
import com.example.amm.mapper.TaskMapper;
import com.example.amm.service.TaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, TaskDO> implements TaskService {

    @Resource
    private TaskMapper taskMapper;


    @Override
    public boolean saveTask(TaskDO taskDO) {
        return taskMapper.insert(taskDO) > 0;
    }

    @Override
    public boolean updateTaskById(Long id, TaskDO taskDO) {
        taskDO.setId(id);
        return taskMapper.updateById(taskDO) > 0;
    }

    @Override
    public boolean removeTaskById(Long id) {
        return taskMapper.deleteById(id) > 0;
    }

    @Override
    public TaskDO getTaskById(Long id) {
        return taskMapper.selectById(id);
    }

    @Override
    public Page<TaskDO> listPage(PageQuery pageQuery) {
        return this.page(new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()), new QueryWrapper<TaskDO>().lambda()
                .orderByDesc(TaskDO::getCreateTime));
    }

    @Override
    public void createQuickTask(TaskDO task) {
        taskMapper.insert(task);
    }
}
