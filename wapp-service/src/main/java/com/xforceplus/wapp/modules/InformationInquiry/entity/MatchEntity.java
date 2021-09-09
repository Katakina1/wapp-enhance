package com.xforceplus.wapp.modules.InformationInquiry.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 匹配表
 */
public class MatchEntity implements Serializable {

    private static final long serialVersionUID = 868730128360121709L;
    private   Long       id;
    private   String     venderId;//供应商号
    private   String     matchNo;//发票组号
    private   String     invoiceNo;//发票号码
    private   BigDecimal taxAmount;//税额
    private   BigDecimal totalAmount;//价税合计
    private   Date       createDate;//录入时间
    private   String     gfName;//购方名称
    private BigDecimal settlemenTamount;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getMatchNo() {
        return matchNo;
    }

    public void setMatchNo(String matchNo) {
        this.matchNo = matchNo;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getGfName() {
        return gfName;
    }

    public void setGfName(String gfName) {
        this.gfName = gfName;
    }

    public BigDecimal getSettlemenTamount() {
        return settlemenTamount;
    }

    public void setSettlemenTamount(BigDecimal settlemenTamount) {
        this.settlemenTamount = settlemenTamount;
    }
}
