package com.xforceplus.wapp.modules.signin.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * CreateBy leal.liang on 2018/4/17.
 **/
@Getter
@Setter
@ToString
public class RecordInvoiceDetailEntity extends AbstractBaseDomain {

    private static final long serialVersionUID = -623712031805025481L;
    //发票代码
    private String invoiceCode;

    //发票号码
    private String invoiceNo;

    //开票日期
    private Date invoiceDate;

    //购方名称
    private String gfName;

    //购方税号
    private String gfTaxNo;

    //购方地址电话
    private String gfAdressndPhone;

    //购方开户账号
    private String gfBankAndNo;

    //销方名称
    private String xfName;
    //购方税号
    private String xfTaxNo;

    //购方地址电话x
    private String xfAdressndPhone;

    //购方开户账号
    private String xfBankAndNo;

    //金额
    private BigDecimal invoiceAmount;

    //税额
    private BigDecimal taxAmount;

    //货物或应税劳务名称
    private String goodsName;

    //规格型号
    private String model;

    //单位
    private String unit;

    //数量
    private String num;

    //单价
    private String unitPrice;

    //税率
   private String taxRate;


    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }


}
