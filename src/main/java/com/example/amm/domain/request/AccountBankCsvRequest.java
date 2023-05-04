package com.example.amm.domain.request;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

@Deprecated
@Data
public class AccountBankCsvRequest {

    @Alias("account_delete_status")
    private Integer accountDeleteStatus;

    @Alias("account_group_id")
    private String accountGroupId;

    @Alias("account_status")
    private Integer accountStatus;

    @Alias("account_unblocke_status")
    private Integer accountUnblockeStatus;

    /**
     * 曾被调查次数
     */
    @Alias("account_unblocke_count")
    private Integer accountUnblockeCount;

    /**
     * 账号申请时间
     */
    @Alias("account_application_time")
    private String accountApplicationTime;

    /**
     * 邮箱
     */
    @Alias("account_email")
    private String accountEmail;

    /**
     * 邮箱密码
     */
    @Alias("account_email_password")
    private String accountEmailPassword;

    /**
     * 用户名或者是Email
     */
    @Alias("account_username")
    private String accountUsername;

    /**
     * 密码
     */
    @Alias("account_password")
    private String accountPassword;

    /**
     * 账号余额
     */
    @Alias("account_balance")
    private String accountBalance;

    /**
     * 转账链接
     */
    @Alias("account_pay_me_link")
    private String accountPayMeLink;

    /**
     * 名
     */
    @Alias("account_first_name")
    private String accountFirstName;

    /**
     * 中间名
     */
    @Alias("account_middle_name")
    private String accountMiddleName;

    /**
     * 姓
     */
    @Alias("account_last_name")
    private String accountLastName;

    /**
     * 性别
     */
    @Alias("account_gender")
    private String accountGender;

    /**
     * 个人信用分
     */
    @Alias("account_credit_score")
    private String accountCreditScore;

    /**
     * 社保号
     */
    @Alias("account_ssn")
    private String accountSsn;

    /**
     * 生日-月
     */
    @Alias("account_month_birthday")
    private String accountMonthBirthday;

    /**
     * 生日-日
     */
    @Alias("account_day_birthday")
    private String accountDayBirthday;

    /**
     * 生日-年
     */
    @Alias("account_year_birthday")
    private String accountYearBirthday;

    /**
     * 街道
     */
    @Alias("account_street")
    private String accountStreet;

    /**
     * 单元号(可为空)
     */
    @Alias("account_steet_apt")
    private String accountSteetApt;

    /**
     * 城市
     */
    @Alias("account_city")
    private String accountCity;

    /**
     * 州
     */
    @Alias("account_state")
    private String accountState;

    /**
     * 邮编
     */
    @Alias("account_zipcode")
    private String accountZipcode;

    /**
     * 国家
     */
    @Alias("account_county")
    private String accountCounty;

    /**
     * 手机号码
     */
    @Alias("account_phone_number")
    private String accountPhoneNumber;

    /**
     * 背调电话
     */
    @Alias("account_history_phone")
    private String accountHistoryPhone;

    /**
     * 驾照号
     */
    @Alias("account_dl_number")
    private String accountDlNumber;

    /**
     * 驾照-街道
     */
    @Alias("account_dl_street")
    private String accountDlStreet;

    /**
     * 驾照-街道单元号
     */
    @Alias("account_dl_steet_apt")
    private String accountDlSteetApt;

    /**
     * 驾照-城市
     */
    @Alias("account_dl_city")
    private String accountDlCity;

    /**
     * 驾照-州
     */
    @Alias("account_dl_state")
    private String accountDlState;

    /**
     * 驾照-邮编
     */
    @Alias("account_dl_zipcode")
    private String accountDlZipcode;

    /**
     * 驾照-申领日期
     */
    @Alias("account_dl_date")
    private String accountDlDate;

    /**
     * 驾照-过期日期
     */
    @Alias("account_dl_exp_date")
    private String accountDlExpDate;

    /**
     * 文件路径
     */
    @Alias("account_file_url")
    private String accountFileUrl;

    /**
     * 文件名
     */
    @Alias("account_file_name")
    private String accountFileName;

    /**
     * 历史路径
     */
    @Alias("account_file_history_path")
    private String accountFileHistoryPath;

    /**
     * #0:未申请 #1:申请成功 #2:申请失败
     */
    @Alias("account_credit_status")
    private Integer accountCreditStatus;

    /**
     * 申请描述
     */
    @Alias("account_credit_reason")
    private String accountCreditReason;

    /**
     * 多日期分割:2023-03-27 13:00:01-----2023-03-28 13:00:01-----2023-03-29 14:00:01;
     */
    @Alias("account_credit_date")
    private String accountCreditDate;

    /**
     * 信用额度
     */
    @Alias("account_credit_balance")
    private String accountCreditBalance;

    /**
     * #0:未申请 #1:申请成功 #2:申请失败
     */
    @Alias("account_cash_bank_status")
    private Integer accountCashBankStatus;

    /**
     * 申请描述
     */
    @Alias("account_cash_bank_reason")
    private String accountCashBankReason;

    /**
     * 多日期分割:2023-03-27 13:00:01-----2023-03-28 13:00:01-----2023-03-29 14:00:01;
     */
    @Alias("account_cash_bank_date")
    private String accountCashBankDate;

    /**
     * 信用卡额度
     */
    @Alias("account_cash_bank_balance")
    private String accountCashBankBalance;

    /**
     * 年收入
     */
    @Alias("account_annual_income")
    private String accountAnnualIncome;

    /**
     * 账号总体概述
     */
    @Alias("account_remark")
    private String accountRemark;

    /**
     * 默认值为:0(正常) ,1:软删除
     */
    @Alias("bank_account_delete_status")
    private Integer bankAccountDeleteStatus;

    /**
     * 银行类型(联查Bank_type表)
     */
    @Alias("bank_type_id")
    private Long bankTypeId;

    /**
     * 0:正常,1:被封
     */
    @Alias("bank_status")
    private Integer bankStatus;

    /**
     * 银行用户名
     */
    @Alias("bank_username")
    private String bankUsername;

    /**
     * 银行密码
     */
    @Alias("bank_password")
    private String bankPassword;

    /**
     * 手机号码
     */
    @Alias("bank_phone_number")
    private String bankPhoneNumber;

    /**
     * 背调电话
     */
    @Alias("bank_history_phone")
    private String bankHistoryPhone;

    /**
     * 邮箱
     */
    @Alias("bank_email")
    private String bankEmail;

    /**
     * 邮箱密码
     */
    @Alias("bank_email_password")
    private String bankEmailPassword;

    /**
     * 路由号
     */
    @Alias("bank_routing_number")
    private String bankRoutingNumber;

    /**
     * 路由账号
     */
    @Alias("bank_account")
    private String bankAccount;

    /**
     * 卡号
     */
    @Alias("bank_card_number")
    private String bankCardNumber;

    /**
     * 卡过期日期
     */
    @Alias("bank_exp_date")
    private String bankExpDate;

    /**
     * 安全码(3位)
     */
    @Alias("bank_cvv")
    private String bankCvv;

    /**
     * 1:Visa 2:Mastercard
     */
    @Alias("bank_card_type")
    private String bankCardType;

    /**
     * 账户余额
     */
    @Alias("bank_balance")
    private String bankBalance;

    /**
     * 名
     */
    @Alias("bank_first_name")
    private String bankFirstName;

    /**
     * 中间名
     */
    @Alias("bank_middle_Name")
    private String bankMiddleName;

    /**
     * 姓
     */
    @Alias("bank_last_name")
    private String bankLastName;

    /**
     * 性别
     */
    @Alias("bank_gender")
    private String bankGender;

    /**
     * 个人信用分
     */
    @Alias("bank_credit_score")
    private String bankCreditScore;

    /**
     * 社保号
     */
    @Alias("bank_ssn")
    private String bankSsn;

    /**
     * 生日-月
     */
    @Alias("bank_month_birthday")
    private String bankMonthBirthday;

    /**
     * 生日-日
     */
    @Alias("bank_day_birthday")
    private String bankDayBirthday;

    /**
     * 生日-年
     */
    @Alias("bank_year_birthday")
    private String bankYearBirthday;

    /**
     * 街道
     */
    @Alias("bank_street")
    private String bankStreet;

    /**
     * 单元号(可为空)
     */
    @Alias("bank_steet_apt")
    private String bankSteetApt;

    /**
     * 城市
     */
    @Alias("bank_city")
    private String bankCity;

    /**
     * 州
     */
    @Alias("bank_state")
    private String bankState;

    /**
     * 邮编
     */
    @Alias("bank_zipcode")
    private String bankZipcode;

    /**
     * 国家
     */
    @Alias("bank_county")
    private String bankCounty;

    /**
     * 驾照号
     */
    @Alias("bank_dl_number")
    private String bankDlNumber;

    /**
     * 驾照-街道
     */
    @Alias("bank_dl_street")
    private String bankDlStreet;

    /**
     * 驾照-街道单元号
     */
    @Alias("bank_dl_steet_apt")
    private String bankDlSteetApt;

    /**
     * 驾照-城市
     */
    @Alias("bank_dl_city")
    private String bankDlCity;

    /**
     * 驾照-州
     */
    @Alias("bank_dl_state")
    private String bankDlState;

    /**
     * 驾照-邮编
     */
    @Alias("bank_dl_zipcode")
    private String bankDlZipcode;

    /**
     * 驾照-申领日期
     */
    @Alias("bank_dl_date")
    private String bankDlDate;

    /**
     * 驾照-过期日期
     */
    @Alias("bank_dl_exp_date")
    private String bankDlExpDate;

    /**
     * 文件路径
     */
    @Alias("bank_file_url")
    private String bankFileUrl;

    /**
     * 文件名
     */
    @Alias("bank_file_name")
    private String bankFileName;

    /**
     * 历史路径
     */
    @Alias("bank_file_history_path")
    private String bankFileHistoryPath;

    /**
     * 账号总体概述
     */
    @Alias("bank_remark")
    private String bankRemark;
}