package com.example.amm.domain.entity;

import com.example.amm.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankTypeDO extends BaseEntity {

    @Schema(description = "")
    private String bankName;

}