package com.xforceplus.wapp.modules.cost.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.math.BigDecimal;


public class ContrastEntity extends AbstractBaseDomain {
    //发票代码
    private String invoiceCode;
    //发票号码
    private String invoiceNo;

    //发票类型 01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票
    private String invoiceType;

    //开票日期
    private String invoiceDate;
    //发票金额
    private BigDecimal invoiceAmount;
    //税额
    private BigDecimal taxAmount;
    //价税合计
    private BigDecimal totalAmount;

    private String venderid;




    //发票代码
    private String invoiceCodeX;
    //发票号码
    private String invoiceNoX;

    //发票类型 01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票
    private String invoiceTypeX;

    //开票日期
    private String invoiceDateX;
    //发票金额
    private BigDecimal invoiceAmountX;
    //税额
    private BigDecimal taxAmountX;
    //价税合计
    private BigDecimal totalAmountX;

    private String venderidX;


    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }

    public String getInvoiceCodeX() {
        return invoiceCodeX;
    }

    public void setInvoiceCodeX(String invoiceCodeX) {
        this.invoiceCodeX = invoiceCodeX;
    }

    public String getInvoiceNoX() {
        return invoiceNoX;
    }

    public void setInvoiceNoX(String invoiceNoX) {
        this.invoiceNoX = invoiceNoX;
    }

    public String getInvoiceTypeX() {
        return invoiceTypeX;
    }

    public void setInvoiceTypeX(String invoiceTypeX) {
        this.invoiceTypeX = invoiceTypeX;
    }

    public String getInvoiceDateX() {
        return invoiceDateX;
    }

    public void setInvoiceDateX(String invoiceDateX) {
        this.invoiceDateX = invoiceDateX;
    }

    public BigDecimal getInvoiceAmountX() {
        return invoiceAmountX;
    }

    public void setInvoiceAmountX(BigDecimal invoiceAmountX) {
        this.invoiceAmountX = invoiceAmountX;
    }

    public BigDecimal getTaxAmountX() {
        return taxAmountX;
    }

    public void setTaxAmountX(BigDecimal taxAmountX) {
        this.taxAmountX = taxAmountX;
    }

    public BigDecimal getTotalAmountX() {
        return totalAmountX;
    }

    public void setTotalAmountX(BigDecimal totalAmountX) {
        this.totalAmountX = totalAmountX;
    }

    public String getVenderidX() {
        return venderidX;
    }

    public void setVenderidX(String venderidX) {
        this.venderidX = venderidX;
    }

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
