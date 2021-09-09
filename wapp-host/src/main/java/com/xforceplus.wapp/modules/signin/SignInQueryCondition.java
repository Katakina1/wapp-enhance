package com.xforceplus.wapp.modules.signin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * CreateBy leal.liang on 2018/4/12.
 **/
@Getter
@Setter
@ToString
public class SignInQueryCondition {

    /**
     * 开票日期
     */
    private String createDate;

    /**
     * 起始开票时间
     */
    private String createStartDate;

    /**
     * 结束开票日期
     */
    private String createEndDate;

    /**
     * 购方名称
     */
    private String gfName;


    /**
     * 发票号码
     */
    private String invoiceNo;

    /**
     * 发票类型 01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票
     */
    private String invoiceType;
}
