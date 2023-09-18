package com.xforceplus.wapp.modules.customs.dto;

import lombok.Data;

@Data
public class CustomsTaxCheckRequest {

    //海关票Id
    private Long customsId;
    //海关缴款书号码
    private String customsPaymentNo;
    //税号
    private String taxNo;
    //版本号 2-2.0账号体系（默认）。 4-4.0账号体系
    private String bb;
    //有效税额
    private String effectiveTaxAmount;
    //开票日期
    private String dateIssued;
    //所属期
    private String taxPeriod;
    //认证用途 1-抵扣勾选 10-撤销抵扣勾选  3-退税勾选 30-退税撤销勾选（确认后无法撤销）
    private String authUse;
    //debug模式勾选
    private String debug;
    //操作用户ID
    private String userId;
    //操作用户名称
    private String userName;
}