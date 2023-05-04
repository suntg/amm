package com.example.amm.domain.entity;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Schema
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("t_task")
public class TaskDO {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @NotBlank(message = "type不能为空")
    private String type;

    /**
     * mybatis-plus中实体字段为mysql关键字时的解决方法—— @TableField("`group`")
     * <p>
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
