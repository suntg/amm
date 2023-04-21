package com.example.amm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.amm.domain.entity.TaskDO;
import com.example.amm.domain.query.PageQuery;
import org.springframework.web.bind.annotation.PathVariable;

public interface TaskService extends IService<TaskDO> {


    boolean saveTask(TaskDO taskDO);

    boolean updateTaskById(Long id, TaskDO taskDO);

    boolean removeTaskById(Long id);

    TaskDO getTaskById(Long id);

    Page<TaskDO> listPage(PageQuery pageQuery);


    void createQuickTask(TaskDO task);

    void uploadLog(Long id, String log);


    void setTaskStatus(Long id, int status);

    int getTaskStatus(Long id);

    void deleteSucTasks();

    void deleteAllTasks();

    void remRedisQueue(Long id, Integer user);


    void updateStatus(Long id, int status);


    void executeTask( Long id);

    void stopTask( Long id);
}
