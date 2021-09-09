package com.xforceplus.wapp.modules.cost.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.math.BigDecimal;

public class MatchRebackEntity  {
    private String invoiceCode;
    private String invoiceNo;
    private BigDecimal costAmount;
    private BigDecimal invoiceAmount;

    //匹配明细值
    private String matchDetailId;



    public String getMatchDetailId() {
        return matchDetailId;
    }

    public void setMatchDetailId(String matchDetailId) {
        this.matchDetailId = matchDetailId;
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

    public BigDecimal getCostAmount() {
        return costAmount;
    }

    public void setCostAmount(BigDecimal costAmount) {
        this.costAmount = costAmount;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }
}
