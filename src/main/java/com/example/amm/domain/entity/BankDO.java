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
public class BankDO extends BaseEntity {

    /**
     * 默认值为:0(正常) ,1:软删除
     */
    @Schema(description = "默认值为:0(正常) ,1:软删除")
    private Integer accountDeleteStatus;

    /**
     * 银行类型(联查Bank_type表)
     */
    @Schema(description = "银行类型(联查Bank_type表)")
    private Long bankTypeId;

    /**
     * 账号id(关联账号表ID)
     */
    @Schema(description = "账号id(关联账号表ID)")
    private Long accountId;

    /**
     * 0:正常,1:被封
     */
    @Schema(description = "0:正常,1:被封")
    private Integer status;

    /**
     * 银行用户名
     */
    @Schema(description = "银行用户名")
    private String username;

    /**
     * 银行密码
     */
    @Schema(description = "银行密码")
    private String password;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    private String phoneNumber;

    /**
     * 背调电话
     */
    @Schema(description = "背调电话")
    private String historyPhone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 邮箱密码
     */
    @Schema(description = "邮箱密码")
    private String emailPassword;

    /**
     * 路由号
     */
    @Schema(description = "路由号")
    private String routingNumber;

    /**
     * 路由账号
     */
    @Schema(description = "路由账号")
    private String bankCccount;

    /**
     * 卡号
     */
    @Schema(description = "卡号")
    private String cardNumber;

    /**
     * 卡过期日期
     */
    @Schema(description = "卡过期日期")
    private String expDate;

    /**
     * 安全码(3位)
     */
    @Schema(description = "安全码(3位)")
    private String cvv;

    /**
     * 1:Visa 2:Mastercard
     */
    @Schema(description = "1:Visa 2:Mastercard")
    private String cardType;

    /**
     * 账户余额
     */
    @Schema(description = "账户余额")
    private String balance;

    /**
     * 名
     */
    @Schema(description = "名")
    private String firstName;

    /**
     * 中间名
     */
    @Schema(description = "中间名")
    private String middleName;

    /**
     * 姓
     */
    @Schema(description = "姓")
    private String lastName;

    /**
     * 性别
     */
    @Schema(description = "性别")
    private String gender;

    /**
     * 个人信用分
     */
    @Schema(description = "个人信用分")
    private String creditScore;

    /**
     * 社保号
     */
    @Schema(description = "社保号")
    private String ssn;

    /**
     * 生日-月
     */
    @Schema(description = "生日-月")
    private String monthBirthday;

    /**
     * 生日-日
     */
    @Schema(description = "生日-日")
    private String dayBirthday;

    /**
     * 生日-年
     */
    @Schema(description = "生日-年")
    private String yearBirthday;

    /**
     * 街道
     */
    @Schema(description = "街道")
    private String street;

    /**
     * 单元号(可为空)
     */
    @Schema(description = "单元号(可为空)")
    private String steetApt;

    /**
     * 城市
     */
    @Schema(description = "城市")
    private String city;

    /**
     * 州
     */
    @Schema(description = "州")
    private String state;

    /**
     * 邮编
     */
    @Schema(description = "邮编")
    private String zipcode;

    /**
     * 国家
     */
    @Schema(description = "国家")
    private String county;

    /**
     * 驾照号
     */
    @Schema(description = "驾照号")
    private String dlNumber;

    /**
     * 驾照-街道
     */
    @Schema(description = "驾照-街道")
    private String dlStreet;

    /**
     * 驾照-街道单元号
     */
    @Schema(description = "驾照-街道单元号")
    private String dlSteetApt;

    /**
     * 驾照-城市
     */
    @Schema(description = "驾照-城市")
    private String dlCity;

    /**
     * 驾照-州
     */
    @Schema(description = "驾照-州")
    private String dlState;

    /**
     * 驾照-邮编
     */
    @Schema(description = "驾照-邮编")
    private String dlZipcode;

    /**
     * 驾照-申领日期
     */
    @Schema(description = "驾照-申领日期")
    private String dlDate;

    /**
     * 驾照-过期日期
     */
    @Schema(description = "驾照-过期日期")
    private String dlExpDate;

    /**
     * 文件路径
     */
    @Schema(description = "文件路径")
    private String fileUrl;

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String fileName;

    /**
     * 历史路径
     */
    @Schema(description = "历史路径")
    private String fileHistoryPath;

    /**
     * 账号总体概述
     */
    @Schema(description = "账号总体概述")
    private String remark;
}