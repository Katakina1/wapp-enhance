package com.xforceplus.wapp.modules.InformationInquiry.entity;

import java.io.Serializable;

public class RedInvoiceUploadEntity implements Serializable {
    //ID
    private Long id;
    //供应商号
    private String venderid;
    //成本金额
    private String redAmount;
    //发票或协议号
    private String invoiceOrAgreementNo;
    //红票信息编号
    private String redInvoiceNo;
    //红票信息生成时间
    private String redInvoiceDate;
    //上传时间
    private String uploadDate;
    //税率
    private String taxRate;
    //税额
    private String taxAmount;
    //供应商名称
    private String venderName;
    //类型
    private String redType;

    public String getRedAmount() {
        return redAmount;
    }

    public void setRedAmount(String redAmount) {
        this.redAmount = redAmount;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
    }

    public String getRedType() {
        return redType;
    }

    public void setRedType(String redType) {
        this.redType = redType;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }


    public String getInvoiceOrAgreementNo() {
        return invoiceOrAgreementNo;
    }

    public void setInvoiceOrAgreementNo(String invoiceOrAgreementNo) {
        this.invoiceOrAgreementNo = invoiceOrAgreementNo;
    }

    public String getRedInvoiceNo() {
        return redInvoiceNo;
    }

    public void setRedInvoiceNo(String redInvoiceNo) {
        this.redInvoiceNo = redInvoiceNo;
    }

    public String getRedInvoiceDate() {
        return redInvoiceDate;
    }

    public void setRedInvoiceDate(String redInvoiceDate) {
        this.redInvoiceDate = redInvoiceDate;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

}
