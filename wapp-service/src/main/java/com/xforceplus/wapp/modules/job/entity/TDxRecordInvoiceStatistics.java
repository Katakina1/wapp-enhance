package com.xforceplus.wapp.modules.job.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TDxRecordInvoiceStatistics implements Serializable{
    private Long id;

    private String invoiceCode;

    private String invoiceNo;

    private BigDecimal detailAmount;

    private BigDecimal taxRate;

    private BigDecimal taxAmount;

    private BigDecimal totalAmount;

    private Date createDate;

    private String depart;

    private String ywzk;

    private BigDecimal zkbl;

    private BigDecimal zkje;

    private String jylx;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode == null ? null : invoiceCode.trim();
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo == null ? null : invoiceNo.trim();
    }

    public BigDecimal getDetailAmount() {
        return detailAmount;
    }

    public void setDetailAmount(BigDecimal detailAmount) {
        this.detailAmount = detailAmount;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
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

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart == null ? null : depart.trim();
    }

    public String getYwzk() {
        return ywzk;
    }

    public void setYwzk(String ywzk) {
        this.ywzk = ywzk == null ? null : ywzk.trim();
    }

    public BigDecimal getZkbl() {
        return zkbl;
    }

    public void setZkbl(BigDecimal zkbl) {
        this.zkbl = zkbl;
    }

    public BigDecimal getZkje() {
        return zkje;
    }

    public void setZkje(BigDecimal zkje) {
        this.zkje = zkje;
    }

    public String getJylx() {
        return jylx;
    }

    public void setJylx(String jylx) {
        this.jylx = jylx == null ? null : jylx.trim();
    }
}