package com.xforceplus.wapp.modules.redTicket.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.math.BigDecimal;
import java.util.List;

/**
 * 发票明细信息表
 */
public class InvoiceDetail extends AbstractBaseDomain {

    private static final long serialVersionUID = -2108155851674283103L;
    private String uuid;//发票代码+发票号码    唯一索引 防重复
    private String invoiceCode;//发票代码
    private String invoiceNo;//发票号码
    private String goodsName;// 货物或应税劳务名称
    private String model;//规格型号
    private String unit;//单位
    private String num;//数量
    private String unitPrice;//单价
    private String detailAmount;//金额
    private String taxRate;//税率
    private BigDecimal taxAmount;//税额
    private Integer redRushNumber;//红冲数量
    private BigDecimal redRushAmount;//红冲金额
    private BigDecimal redRushTaxAmount;//红冲税额
    private BigDecimal redRushPrice;//红冲单价
    private String whetherRedRush;//是否红冲
    private String redTicketDataSerialNumber;//序列号
    private List<InvoiceDetail> invoiceDetails;

    public BigDecimal getRedRushTaxAmount() {
        return redRushTaxAmount;
    }

    public void setRedRushTaxAmount(BigDecimal redRushTaxAmount) {
        this.redRushTaxAmount = redRushTaxAmount;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getDetailAmount() {
        return detailAmount;
    }

    public void setDetailAmount(String detailAmount) {
        this.detailAmount = detailAmount;
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

    public Integer getRedRushNumber() {
        return redRushNumber;
    }

    public void setRedRushNumber(Integer redRushNumber) {
        this.redRushNumber = redRushNumber;
    }

    public BigDecimal getRedRushAmount() {
        return redRushAmount;
    }

    public void setRedRushAmount(BigDecimal redRushAmount) {
        this.redRushAmount = redRushAmount;
    }

    public BigDecimal getRedRushPrice() {
        return redRushPrice;
    }

    public void setRedRushPrice(BigDecimal redRushPrice) {
        this.redRushPrice = redRushPrice;
    }

    public String getWhetherRedRush() {
        return whetherRedRush;
    }

    public void setWhetherRedRush(String whetherRedRush) {
        this.whetherRedRush = whetherRedRush;
    }

    public String getRedTicketDataSerialNumber() {
        return redTicketDataSerialNumber;
    }

    public void setRedTicketDataSerialNumber(String redTicketDataSerialNumber) {
        this.redTicketDataSerialNumber = redTicketDataSerialNumber;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<InvoiceDetail> getInvoiceDetails() {
        return invoiceDetails;
    }

    public void setInvoiceDetails(List<InvoiceDetail> invoiceDetails) {
        this.invoiceDetails = invoiceDetails;
    }

    @Override
    public Boolean isNullObject() {
        return null;
    }
}
