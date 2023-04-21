package com.example.amm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.amm.common.BizException;
import com.example.amm.common.ReturnCode;
import com.example.amm.constant.RedisKeyConstant;
import com.example.amm.domain.entity.TaskDO;
import com.example.amm.domain.query.PageQuery;
import com.example.amm.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Tag(name = "Task")
@Slf4j
@RestController
@RequestMapping("/task")
public class TaskController {

    @Resource
    private TaskService taskService;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Operation(summary = "分页查询")
    @GetMapping("/listPage")
    public Page<TaskDO> listPage(PageQuery pageQuery) {
        return taskService.listPage(pageQuery);
    }

    @PostMapping("/save")
    public boolean saveTask(@RequestBody @Validated TaskDO task) {
        return taskService.saveTask(task);
    }

    @PutMapping("/update/{id}")
    public boolean updateTaskById(@PathVariable Long id, @RequestBody TaskDO task) {
        return taskService.updateTaskById(id, task);
    }

    @DeleteMapping("/delete/{id}")
    public boolean removeTaskById(@PathVariable Long id) {
        redisTemplate.delete(RedisKeyConstant.LOG_TASK_KEY + id);
        return taskService.removeTaskById(id);
    }

    @GetMapping("/{id}")
    public TaskDO getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PostMapping("/createQuick")
    public void createQuick(@RequestBody TaskDO task) {
        if (Integer.parseInt(task.getMoney()) < 0) {
            throw new BizException(ReturnCode.LIMIT_ERROR.getCode(), "金额 必须 > 0");
        }

        if (task.getType().length() < 2) {
            throw new BizException(ReturnCode.LIMIT_ERROR.getCode(), "Type 类型不对");
        }

        taskService.createQuickTask(task);
    }

    @Operation(summary = "上传日志")
    @PostMapping("/uploadLog/{id}")
    public void uploadLog(@PathVariable("id") Long id, @RequestBody String log) {
        taskService.uploadLog(id, log);
    }


    @Operation(summary = "获取 log")
    @GetMapping("/log/{id}")
    public List<String> getGroupLogListById(@PathVariable String id) {
        return redisTemplate.opsForList().range(RedisKeyConstant.LOG_TASK_KEY + id, 0, -1);
    }

}

