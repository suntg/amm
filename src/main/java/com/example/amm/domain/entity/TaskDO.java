package com.example.amm.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.amm.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Schema
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_task")
public class TaskDO extends BaseEntity {


    @NotBlank(message = "type不能为空")
    private String type;

    /**
     *
     * mybatis-plus中实体字段为mysql关键字时的解决方法—— @TableField("`group`")
     *
     * 在SQL中INSERT INTO t_task ( `group`) VALUES (0);
     */
    @Schema(description = "分组")
    @NotNull(message = "group不能为空")
    @TableField("`group`")
    private Integer group;

    private String money;

    private Integer status;

    /**
     *
     */
    @Schema(description = "")
    private String remark;
}
