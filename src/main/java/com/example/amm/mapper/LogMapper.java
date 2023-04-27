package com.example.amm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.amm.domain.entity.LogDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogMapper extends BaseMapper<LogDO> {
}