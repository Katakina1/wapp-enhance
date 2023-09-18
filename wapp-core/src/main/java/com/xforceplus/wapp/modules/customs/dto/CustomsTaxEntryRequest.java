package com.xforceplus.wapp.modules.customs.dto;

import lombok.Data;

@Data
public class CustomsTaxEntryRequest {

    //海关票Id
    private Long customsId;
    //海关缴款书号码
    private String customsPaymentNo;
    //税号
    private String taxNo;
    //版本号 2-2.0账号体系（默认）。 4-4.0账号体系
    private String bb;
    //02-入账（企业所得税税前扣除）,03-入账（企业所得税不扣除）,06-入账撤销
    private String entryStatus;
    //debug模式勾选
    private String debug;
    //操作用户ID
    private String userId;
    //操作用户名称
    private String userName;
}