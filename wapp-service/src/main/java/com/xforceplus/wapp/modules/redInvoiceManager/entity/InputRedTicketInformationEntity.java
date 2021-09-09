package com.xforceplus.wapp.modules.redInvoiceManager.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class InputRedTicketInformationEntity implements Serializable {
    //ID
    private Long id;

    //红字通知单号
    private String redLetterNotice;
    private String redNoticeNumber;

    //上传日期
    private String createDate;

    //税率
    private BigDecimal taxRate;

    //发票代码
    private String invoiceCode;

    //发票号码
    private String invoiceNo;

    //发票类型
    private String invoiceType;

    //开票日期
    private String invoiceDate;

    //金额
    private BigDecimal invoiceAmount;

    //总金额
    private BigDecimal totalInvoiceAmount;

    //税额
    private BigDecimal taxAmount;

    //价税合计
    private BigDecimal totalAmount;


    private String schemaLabel;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRedLetterNotice() {
        return redLetterNotice;
    }

    public void setRedLetterNotice(String redLetterNotice) {
        this.redLetterNotice = redLetterNotice;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

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

    public String getSchemaLabel() {
        return schemaLabel;
    }

    public void setSchemaLabel(String schemaLabel) {
        this.schemaLabel = schemaLabel;
    }

    public String getRedNoticeNumber() {
        return redNoticeNumber;
    }

    public void setRedNoticeNumber(String redNoticeNumber) {
        this.redNoticeNumber = redNoticeNumber;
    }

    public BigDecimal getTotalInvoiceAmount() {
        return totalInvoiceAmount;
    }

    public void setTotalInvoiceAmount(BigDecimal totalInvoiceAmount) {
        this.totalInvoiceAmount = totalInvoiceAmount;
    }
}
