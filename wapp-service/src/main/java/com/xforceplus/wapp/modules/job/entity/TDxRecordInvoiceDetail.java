package com.xforceplus.wapp.modules.job.entity;

import java.io.Serializable;

public class TDxRecordInvoiceDetail implements Serializable {
    private Long id;

    private String uuid;

    private String invoiceCode;

    private String invoiceNo;

    private String detailNo;

    private String goodsName;

    private String model;

    private String unit;

    private String num;

    private String unitPrice;

    private String detailAmount;

    private String taxRate;

    private String taxAmount;

    private String cph;

    private String lx;

    private String txrqq;

    private String txrqz;

    private String goodsNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
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

    public String getDetailNo() {
        return detailNo;
    }

    public void setDetailNo(String detailNo) {
        this.detailNo = detailNo == null ? null : detailNo.trim();
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName == null ? null : goodsName.trim();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model == null ? null : model.trim();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit == null ? null : unit.trim();
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num == null ? null : num.trim();
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice == null ? null : unitPrice.trim();
    }

    public String getDetailAmount() {
        return detailAmount;
    }

    public void setDetailAmount(String detailAmount) {
        this.detailAmount = detailAmount == null ? null : detailAmount.trim();
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate == null ? null : taxRate.trim();
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount == null ? null : taxAmount.trim();
    }

    public String getCph() {
        return cph;
    }

    public void setCph(String cph) {
        this.cph = cph == null ? null : cph.trim();
    }

    public String getLx() {
        return lx;
    }

    public void setLx(String lx) {
        this.lx = lx == null ? null : lx.trim();
    }

    public String getTxrqq() {
        return txrqq;
    }

    public void setTxrqq(String txrqq) {
        this.txrqq = txrqq == null ? null : txrqq.trim();
    }

    public String getTxrqz() {
        return txrqz;
    }

    public void setTxrqz(String txrqz) {
        this.txrqz = txrqz == null ? null : txrqz.trim();
    }

    public String getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(String goodsNum) {
        this.goodsNum = goodsNum == null ? null : goodsNum.trim();
    }
}