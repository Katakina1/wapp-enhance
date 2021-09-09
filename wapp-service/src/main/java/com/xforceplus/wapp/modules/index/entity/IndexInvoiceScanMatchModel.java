package com.xforceplus.wapp.modules.index.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Bobby
 * @date 2018/4/16
 * 首页-发票扫描
 */
public final class IndexInvoiceScanMatchModel {



    private Long id;

    //生成红票资料序列号
    private String redticketDataSerialNumber;

    //红冲总金额
    private BigDecimal redTotalAmount;

    //创建时间
    private Date redticketCreationTime;

    /**
     * 发票类型（01-增值税专用发票
     * 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票）
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

    //红票税率
    private BigDecimal taxRate;


    //供应商id
    private String venderId;


    /**
     * 购方税号
     */
    private String gfTaxNo;

    /**
     * 金额
     */
    private BigDecimal invoiceAmount;

    /**
     * 税额
     */
    private BigDecimal taxAmount;

    /**
     * 价税合计
     */
    private BigDecimal totalAmount;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRedticketDataSerialNumber() {
        return redticketDataSerialNumber;
    }

    public void setRedticketDataSerialNumber(String redticketDataSerialNumber) {
        this.redticketDataSerialNumber = redticketDataSerialNumber;
    }

    public BigDecimal getRedTotalAmount() {
        return redTotalAmount;
    }

    public void setRedTotalAmount(BigDecimal redTotalAmount) {
        this.redTotalAmount = redTotalAmount;
    }

    public Date getRedticketCreationTime() {
        return redticketCreationTime;
    }

    public void setRedticketCreationTime(Date redticketCreationTime) {
        this.redticketCreationTime = redticketCreationTime;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
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

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getGfTaxNo() {
        return gfTaxNo;
    }

    public void setGfTaxNo(String gfTaxNo) {
        this.gfTaxNo = gfTaxNo;
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
}
