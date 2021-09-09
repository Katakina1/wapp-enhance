package com.xforceplus.wapp.modules.lease.entity;

import java.io.Serializable;

public class InvoiceImportAndExportImportEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private int  id;
    private String uuid;
    private String  companyCode;
    private String  invoiceCode;
    private String  invoiceType;
    private String  invoiceNo;
    private String  venderId;
    private String  venderName;
    private String  taxAmount;
    private String  taxRate;
    private String  invoiceAmount;
    private String  invoiceDate;
    private String  reMark;


    private String  jvCode;
    private String  shopNo;
    private String  peRiod;
    private String  matChing;
    private String  matChingDate;
    private String  sAp;
    private Boolean atrue;
    private Integer errorCount;
    private String sApDate;
    private String taxCode;
    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }


    public String getsApDate() {
        return sApDate;
    }

    public void setsApDate(String sApDate) {
        this.sApDate = sApDate;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getReMark() {
        return reMark;
    }

    public void setReMark(String reMark) {
        this.reMark = reMark;
    }

    public String getJvCode() {
        return jvCode;
    }

    public void setJvCode(String jvCode) {
        this.jvCode = jvCode;
    }

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public String getPeRiod() {
        return peRiod;
    }

    public void setPeRiod(String peRiod) {
        this.peRiod = peRiod;
    }

    public String getMatChing() {
        return matChing;
    }

    public void setMatChing(String matChing) {
        this.matChing = matChing;
    }

    public String getMatChingDate() {
        return matChingDate;
    }

    public void setMatChingDate(String matChingDate) {
        this.matChingDate = matChingDate;
    }

    public String getsAp() {
        return sAp;
    }

    public void setsAp(String sAp) {
        this.sAp = sAp;
    }

    public Boolean getAtrue() {
        return atrue;
    }

    public void setAtrue(Boolean atrue) {
        this.atrue = atrue;
    }
}
