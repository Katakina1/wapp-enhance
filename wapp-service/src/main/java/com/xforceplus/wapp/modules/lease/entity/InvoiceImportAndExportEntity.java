package com.xforceplus.wapp.modules.lease.entity;

import java.io.Serializable;
import java.util.Date;

public class InvoiceImportAndExportEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long  id;
    private String uuid;
    private String  companyCode;
    private String  invoiceCode;
    private String  invoiceType;
    private String  invoiceNo;
    private String  venderId;
    private String  venderName;
    private Double  taxAmount;
    private Double  taxRate;
    private Double  invoiceAmount;
    private Double  totalAmount;
    private Date    invoiceDate;
    private String  reMark;
    private String  jvCode;
    private String  shopNo;
    private String  peRiod;
    private String  matChing;
    private Date  matChingDate;
    private String sap;
    private Date sapDate;
    private String orders;
    private String taxCode;
    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getSapDate() {
        return sapDate;
    }

    public void setSapDate(Date sapDate) {
        this.sapDate = sapDate;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Double getTaxAmount() {
        if(taxAmount == null){
            return 0.00;
        }
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        if(taxAmount == null){
            this.taxAmount=0.00;
        }
        this.taxAmount = taxAmount;
    }

    public Double getTaxRate() {
        if(taxRate == null){
            return 0.00;
        }
        return taxRate;
    }

    public void setTaxRate(Double taxRate) {
        if(taxRate == null){
            this.taxRate= 0.00;
        }
        this.taxRate = taxRate;
    }

    public Double getInvoiceAmount() {
        if(invoiceAmount == null){
            return 0.00;
        }
        return invoiceAmount;
    }

    public void setInvoiceAmount(Double invoiceAmount) {
        if(invoiceAmount == null){
            this.invoiceAmount=0.00;
        }
        this.invoiceAmount = invoiceAmount;
    }

    public Double getTotalAmount() {
        if(totalAmount == null){
            return 0.00;
        }
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        if(totalAmount == null){
            this.totalAmount = 0.00;
        }
        this.totalAmount = totalAmount;
    }



    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
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

    public Date getMatChingDate() {
        return matChingDate;
    }

    public void setMatChingDate(Date matChingDate) {
        this.matChingDate = matChingDate;
    }

    public String getSap() {
        return sap;
    }

    public void setSap(String sap) {
        this.sap = sap;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }
}
