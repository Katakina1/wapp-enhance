package com.xforceplus.wapp.modules.job.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.xforceplus.wapp.modules.job.pojo.BasePojo;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class CheckNoDetailInvoice extends BasePojo{

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
    private Date invoiceDate;

    /**
     * 校验吗
     */
    private String checkCode;

    /**
     * 金额
     */
    private BigDecimal invoiceAmount;
}
