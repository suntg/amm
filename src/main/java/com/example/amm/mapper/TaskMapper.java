package com.example.amm.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.amm.domain.entity.TaskDO;

@Mapper
public interface TaskMapper extends BaseMapper<TaskDO> {

}