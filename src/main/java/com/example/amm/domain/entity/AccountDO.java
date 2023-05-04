package com.example.amm.domain.entity;

import java.time.LocalDateTime;

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
@TableName("t_account")
public class AccountDO {

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     *
     */
    @Schema(description = "分组")
    @TableField("`group`")
    private Integer group;

    private Integer groupStatus;

    @Schema(description = "邮箱")
    private String title;

    /**
     * 账号余额
     */
    @Schema(description = "账号余额")
    private String balance;

    /**
     * 路径
     */
    @Schema(description = "路径")
    private String path;

    /**
     * 转账链接
     */
    @Schema(description = "转账链接")
    private String payme;

    @Schema(description = "")
    private Integer switchIp;

    @Schema(description = "")
    private Integer packageNum;

    @TableLogic
    private Integer deleted;

    private Integer status;

    private int succTimes;

    private String succMoney;

    private String recentLog;

    private String affiliation;

    private String batch;

    /**
     *
     */
    @Schema(description = "")
    private String remark;

    @TableField(exist = false)
    private int changeTimeFlag;

    private LocalDateTime firstTime;

    private LocalDateTime deleteTime;

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
    private LocalDateTime updateTime;

    // /**
    // * 默认值为:0(正常) ,1:软删除
    // */
    // @Schema(description = "默认值为:0(正常) ,1:软删除")
    // private Integer accountDeleteStatus;
    //
    // /**
    // * 分组身份ID
    // */
    // @Schema(description = "分组身份ID")
    // private String groupId;
    //
    // /**
    // * 0:正常,1:被调查,2:已封号
    // */
    // @Schema(description = "0:正常,1:被调查,2:已封号")
    // private Integer accountStatus;
    //
    // /**
    // * 0:从未被调查过,1:曾被调查过
    // */
    // @Schema(description = "0:从未被调查过,1:曾被调查过")
    // private Integer unblockeStatus;
    //
    // /**
    // * 曾被调查次数
    // */
    // @Schema(description = "曾被调查次数")
    // private Integer unblockeCount;
    //
    // /**
    // * 账号申请时间
    // */
    // @Schema(description = "账号申请时间")
    // private String accountApplicationTime;
    //
    //
    // /**
    // * 邮箱密码
    // */
    // @Schema(description = "邮箱密码")
    // private String emailPassword;
    //
    // /**
    // * 用户名或者是Email
    // */
    // @Schema(description = "用户名或者是Email")
    // private String username;
    //
    // /**
    // * 密码
    // */
    // @Schema(description = "密码")
    // private String password;
    //
    // /**
    // * 名
    // */
    // @Schema(description = "名")
    // private String firstName;
    //
    // /**
    // * 中间名
    // */
    // @Schema(description = "中间名")
    // private String middleName;
    //
    // /**
    // * 姓
    // */
    // @Schema(description = "姓")
    // private String lastName;
    //
    // /**
    // * 性别
    // */
    // @Schema(description = "性别")
    // private String gender;
    //
    // /**
    // * 个人信用分
    // */
    // @Schema(description = "个人信用分")
    // private String creditScore;
    //
    // /**
    // * 社保号
    // */
    // @Schema(description = "社保号")
    // private String ssn;
    //
    // /**
    // * 生日-月
    // */
    // @Schema(description = "生日-月")
    // private String monthBirthday;
    //
    // /**
    // * 生日-日
    // */
    // @Schema(description = "生日-日")
    // private String dayBirthday;
    //
    // /**
    // * 生日-年
    // */
    // @Schema(description = "生日-年")
    // private String yearBirthday;
    //
    // /**
    // * 街道
    // */
    // @Schema(description = "街道")
    // private String street;
    //
    // /**
    // * 单元号(可为空)
    // */
    // @Schema(description = "单元号(可为空)")
    // private String steetApt;
    //
    // /**
    // * 城市
    // */
    // @Schema(description = "城市")
    // private String city;
    //
    // /**
    // * 州
    // */
    // @Schema(description = "州")
    // private String state;
    //
    // /**
    // * 邮编
    // */
    // @Schema(description = "邮编")
    // private String zipcode;
    //
    // /**
    // * 国家
    // */
    // @Schema(description = "国家")
    // private String county;
    //
    // /**
    // * 手机号码
    // */
    // @Schema(description = "手机号码")
    // private String phoneNumber;
    //
    // /**
    // * 背调电话
    // */
    // @Schema(description = "背调电话")
    // private String historyPhone;
    //
    // /**
    // * 驾照号
    // */
    // @Schema(description = "驾照号")
    // private String dlNumber;
    //
    // /**
    // * 驾照-街道
    // */
    // @Schema(description = "驾照-街道")
    // private String dlStreet;
    //
    // /**
    // * 驾照-街道单元号
    // */
    // @Schema(description = "驾照-街道单元号")
    // private String dlSteetApt;
    //
    // /**
    // * 驾照-城市
    // */
    // @Schema(description = "驾照-城市")
    // private String dlCity;
    //
    // /**
    // * 驾照-州
    // */
    // @Schema(description = "驾照-州")
    // private String dlState;
    //
    // /**
    // * 驾照-邮编
    // */
    // @Schema(description = "驾照-邮编")
    // private String dlZipcode;
    //
    // /**
    // * 驾照-申领日期
    // */
    // @Schema(description = "驾照-申领日期")
    // private String dlDate;
    //
    // /**
    // * 驾照-过期日期
    // */
    // @Schema(description = "驾照-过期日期")
    // private String dlExpDate;
    //
    // /**
    // * 文件路径
    // */
    // @Schema(description = "文件路径")
    // private String fileUrl;
    //
    // /**
    // * 文件名
    // */
    // @Schema(description = "文件名")
    // private String fileName;
    //
    // /**
    // * 历史路径
    // */
    // @Schema(description = "历史路径")
    // private String fileHistoryPath;
    //
    // /**
    // * #0:未申请 #1:申请成功 #2:申请失败
    // */
    // @Schema(description = "#0:未申请 #1:申请成功 #2:申请失败")
    // private Integer creditStatus;
    //
    // /**
    // * 申请描述
    // */
    // @Schema(description = "申请描述")
    // private String creditReason;
    //
    // /**
    // * 多日期分割:2023-03-27 13:00:01-----2023-03-28 13:00:01-----2023-03-29 14:00:01;
    // */
    // @Schema(description = "多日期分割:2023-03-27 13:00:01-----2023-03-28 13:00:01-----2023-03-29 14:00:01;")
    // private String creditDate;
    //
    // /**
    // * 信用额度
    // */
    // @Schema(description = "信用额度")
    // private String creditBalance;
    //
    // /**
    // * #0:未申请 #1:申请成功 #2:申请失败
    // */
    // @Schema(description = "#0:未申请 #1:申请成功 #2:申请失败")
    // private Integer cashBankStatus;
    //
    // /**
    // * 申请描述
    // */
    // @Schema(description = "申请描述")
    // private String cashBankReason;
    //
    // /**
    // * 多日期分割:2023-03-27 13:00:01-----2023-03-28 13:00:01-----2023-03-29 14:00:01;
    // */
    // @Schema(description = "多日期分割:2023-03-27 13:00:01-----2023-03-28 13:00:01-----2023-03-29 14:00:01;")
    // private String cashBankDate;
    //
    // /**
    // * 信用卡额度
    // */
    // @Schema(description = "信用卡额度")
    // private String cashBankBalance;
    //
    // /**
    // * 年收入
    // */
    // @Schema(description = "年收入")
    // private String annualIncome;

}