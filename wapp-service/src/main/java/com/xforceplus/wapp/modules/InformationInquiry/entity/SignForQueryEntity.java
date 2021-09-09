package com.xforceplus.wapp.modules.InformationInquiry.entity;


import java.io.Serializable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 发票匹配
 */
public class SignForQueryEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    //发票代码
    private String invoiceCode;
    //发票号码
    private String invoiceNo;
    //开票日期
    private Date invoiceDate;

    //签收日期
    private Date qsDate;

    //签收状态
    private String qsStatus;


    //购方名称
    private String gfName;

    //销方名称
    private String xfName;

    //金额
    private BigDecimal invoiceAmount;

    //税额
    private BigDecimal taxAmount;

    private String jvCode;
    private String companyCode;

    private String invoiceType;
    private  String venderid;
    private String notes;
    private String invoiceStatus;
    private String flowType;
    private String scanMatchStatus;
    private String scanFailReason;
    private String scanId;
    private String epsNo;
    private  String isdel;
    public String getIsdel() {
        return isdel;
    }

    public void setIsdel(String isdel) {
        this.isdel = isdel;
    }

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
        this.invoiceCode = invoiceCode;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getQsStatus() {
        return qsStatus;
    }

    public void setQsStatus(String qsStatus) {
        this.qsStatus = qsStatus;
    }

    public String getGfName() {
        return gfName;
    }

    public void setGfName(String gfName) {
        this.gfName = gfName;
    }

    public String getXfName() {
        return xfName;
    }

    public void setXfName(String xfName) {
        this.xfName = xfName;
    }

    public BigDecimal getInvoiceAmount() {
        if(invoiceAmount == null){
            return new BigDecimal("0.0000");
        }
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        if(invoiceAmount == null){
            this.invoiceAmount = new BigDecimal("0.0000");
        }
        this.invoiceAmount = invoiceAmount;
    }



    public Date getQsDate() {
        if(qsDate == null){
            return null;
        }
        return (Date) qsDate.clone();
    }

    public BigDecimal getTaxAmount() {
        if(taxAmount==null){
            return new BigDecimal("0.0000");
        }
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        if(taxAmount==null){
            this.taxAmount = new BigDecimal("0.0000");
        }
        this.taxAmount = taxAmount;
    }

    public void setQsDate(Date qsDate) {
        if (qsDate == null) {
            this.qsDate = null;
        }else {
            this.qsDate = (Date) qsDate.clone();
        }
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getJvCode() {
        return jvCode;
    }

    public void setJvCode(String jvCode) {
        this.jvCode = jvCode;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }


    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public String getScanMatchStatus() {
        return scanMatchStatus;
    }

    public void setScanMatchStatus(String scanMatchStatus) {
        this.scanMatchStatus = scanMatchStatus;
    }

    public String getScanFailReason() {
        return scanFailReason;
    }

    public void setScanFailReason(String scanFailReason) {
        this.scanFailReason = scanFailReason;
    }

	public String getScanId() {
		return scanId;
	}

	public void setScanId(String scanId) {
		this.scanId = scanId;
	}

    public String getEpsNo() {
        return epsNo;
    }

    public void setEpsNo(String epsNo) {
        this.epsNo = epsNo;
    }
}
