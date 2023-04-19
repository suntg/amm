package com.example.amm.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.amm.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Schema
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_task")
public class TaskDO extends BaseEntity {


    private String type;

    /**
     * 分组身份ID
     */
    @Schema(description = "分组身份ID")
    private Integer group;

    private String money;

    private Integer status;

    /**
     *
     */
    @Schema(description = "")
    private String remark;
}
