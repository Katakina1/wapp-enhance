package com.xforceplus.wapp.modules.check.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Bobby
 * @date 2018/4/23
 */
@Getter
@Setter
public final class InvoiceCheckRequestParam  {

    /**
     * 购方税号
     */
    private String buyerTaxNo;

    /**
     * 发票类型
     */
    private String invoiceType;

    /**
     * 发票代码
     */
    private String invoiceCode;

    /**
     * 发票号码
     */
    private String invoiceNo;

    /**
     * 开票时间
     */
    private String invoiceDate;

    /**
     * 校验吗
     */
    private String checkCode;

    /**
     * 金额
     */
    private String invoiceAmount;
}
