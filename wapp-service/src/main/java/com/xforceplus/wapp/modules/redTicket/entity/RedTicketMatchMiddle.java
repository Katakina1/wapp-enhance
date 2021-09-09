package com.xforceplus.wapp.modules.redTicket.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 发票匹配中间表
 */
public class RedTicketMatchMiddle extends AbstractBaseDomain {


    private static final long serialVersionUID = 998255463544432937L;
    private String invoiceCode;//发票编码
    private String invoiceNo;//发票号
    private String invoiceType;//发票类型
    private String invoiceAmount;//发票金额
    private String taxRate;//税率
    private BigDecimal redRushAmount;//本次红冲金额
    private BigDecimal taxAmount;//税额
    private Date invoiceDate;//开票日期
    private String gfTaxNo;//购方税号
    private BigDecimal totalAmount;//价税合计
    private Integer redTicketMatchingAssociation;//关联字段（与红票匹配表id关联）

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }


    public BigDecimal getRedRushAmount() {
        return redRushAmount;
    }

    public void setRedRushAmount(BigDecimal redRushAmount) {
        this.redRushAmount = redRushAmount;
    }

    public Integer getRedTicketMatchingAssociation() {
        return redTicketMatchingAssociation;
    }

    public void setRedTicketMatchingAssociation(Integer redTicketMatchingAssociation) {
        this.redTicketMatchingAssociation = redTicketMatchingAssociation;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getGfTaxNo() {
        return gfTaxNo;
    }

    public void setGfTaxNo(String gfTaxNo) {
        this.gfTaxNo = gfTaxNo;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    @Override
    public Boolean isNullObject() {
        return null;
    }
}
