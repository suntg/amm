package com.example.amm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.amm.common.BizException;
import com.example.amm.common.ReturnCode;
import com.example.amm.constant.BusinessType;
import com.example.amm.constant.User;
import com.example.amm.domain.entity.LogDO;
import com.example.amm.domain.entity.TaskDO;
import com.example.amm.domain.query.PageQuery;
import com.example.amm.service.LogService;
import com.example.amm.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Task")
@Slf4j
@RestController
@RequestMapping("/task")
public class TaskController {

    @Resource
    private TaskService taskService;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Operation(summary = "分页查询 index()")
    @GetMapping("/listPage")
    public Page<TaskDO> listPage(PageQuery pageQuery) {
        return taskService.listPage(pageQuery);
    }

    @Operation(summary = "creat()")
    @PostMapping("/save")
    public boolean saveTask(@RequestBody @Validated TaskDO task) {
        return taskService.saveTask(task);
    }

    @Operation(summary = "update()")
    @PutMapping("/update/{id}")
    public boolean updateTaskById(@PathVariable Long id, @RequestBody TaskDO task) {
        return taskService.updateTaskById(id, task);
    }

    @Operation(summary = "del($id)")
    @DeleteMapping("/delete/{id}")
    @Transactional(rollbackFor = Exception.class)
    public boolean removeTaskById(@PathVariable Long id) {

        logService.remove(new QueryWrapper<LogDO>().lambda().eq(LogDO::getBusiness, BusinessType.TASK.toString()).eq(LogDO::getBusinessId, id));

        // redisTemplate.delete(RedisKeyConstant.TASK_LOG_KEY + id);
        return taskService.removeTaskById(id);
    }

    @Operation(summary = "通过id查询 edit($id)")
    @GetMapping("/{id}")
    public TaskDO getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @Operation(summary = "creat_quick()")
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

    @Operation(summary = "上传日志  setlog($id,$value)")
    @PostMapping("/uploadLog/{id}")
    public void uploadLog(@PathVariable("id") Long id, @RequestBody String log) {
        taskService.uploadLog(id, log);
    }

    @Resource
    private LogService logService;

    @Operation(summary = "获取 log  viewlog($id)")
    @GetMapping("/log/{id}")
    public List<String> getLogListById(@PathVariable String id) {
        // redisTemplate.opsForList().range(RedisKeyConstant.TASK_LOG_KEY + id, 0, -1);

        return logService.list(new QueryWrapper<LogDO>().lambda().eq(LogDO::getBusinessId, id)
                        .eq(LogDO::getBusiness, BusinessType.TASK.toString()).orderByDesc(LogDO::getLogTime).orderByDesc(LogDO::getId))
                .stream().map(LogDO::getMessage).collect(Collectors.toList());

    }


    @Operation(summary = "setstatus($id,$status)")
    @PostMapping("/setStatus/{id}/{status}")
    public int setStatus(@PathVariable Long id, @PathVariable int status) {
        return taskService.setTaskStatus(id, status);
    }


    @Operation(summary = "一键清空已完成任务 delsuctask()")
    @GetMapping("/delSucTask")
    public void delSucTask() {
        taskService.deleteSucTasks();
    }


    @Operation(summary = "一键清空所有任务 delalltask()")
    @GetMapping("/delAllTask")
    @Transactional(rollbackFor = Exception.class)
    public void delAllTask() {
        taskService.deleteAllTasks();
        // 清空自动日志的redis
        // autopp_25_autologs

        logService.remove(new QueryWrapper<LogDO>().lambda().eq(LogDO::getBusiness, BusinessType.AUTO_USER.toString()).eq(LogDO::getBusinessId, User.USER_ID_25));
        // redisTemplate.delete("autopp_25_autologs");
    }


    @Operation(summary = " gotorun($id)")
    @GetMapping("/executeTask/{id}")
    public void executeTask(@PathVariable Long id) {
        taskService.executeTask(id);
    }

    @Operation(summary = " gotostop($id)")
    @GetMapping("/stopTask/{id}")
    public void stopTask(@PathVariable Long id) {
        taskService.stopTask(id);
    }


    @Operation(summary = "allrun()")
    @GetMapping("/executeAllTask")
    public void executeAllTask() {
        List<TaskDO> taskDOList = taskService.list(new QueryWrapper<TaskDO>().lambda().eq(TaskDO::getStatus, 9));
        for (TaskDO taskDO : taskDOList) {
            executeTask(taskDO.getId());
        }
    }

    @Operation(summary = "get_redis_queue()")
    @GetMapping("/getTaskQueueElement")
    public String getTaskQueueElement() {
        String key = "auto_25_taskqueue";
        return redisTemplate.opsForList().rightPop(key);
    }


}

