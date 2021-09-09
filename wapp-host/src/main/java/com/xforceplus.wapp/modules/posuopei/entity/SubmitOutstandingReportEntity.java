package com.xforceplus.wapp.modules.posuopei.entity;

import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author raymond.yan
 */
public class SubmitOutstandingReportEntity extends BaseEntity implements Serializable {
    private Long id;
    private String datet;
    private String inputUser;
    private String jv;
    private String vendorNo;
    private String invNo;
    private String invoiceCost;
    private String wmCost;
    private String batchId;
    private String poNo;
    private String trans;
    private String rece;
    private String errcode;
    private String errdesc;
    private String errstatus;
    private String invoiceDate;
    /**
     * 税额
     */
    private String taxAmount;
    /**
     * 税率
     */
    private String taxRate;
    /**
     * 发票类型 01 专票 04 普票
     */
    private  String taxType;

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

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public SubmitOutstandingReportEntity(){}
    public SubmitOutstandingReportEntity(String datet,String inputUser,String jv,String vendorNo,String invNo,String invoiceCost,String wmCost,String batchId,String poNo,String trans,String rece,String errcode,String errdesc,String errstatus){
        this.datet=datet;
        this.inputUser=inputUser;
        this.jv=jv;
        this.vendorNo=vendorNo;
        this.invNo=invNo;
        this.invoiceCost=invoiceCost;
        this.wmCost=wmCost;
        this.batchId=batchId;
        this.poNo=poNo;
        this.trans=trans;
        this.rece=rece;
        this.errcode=errcode;
        this.errdesc=errdesc;
        this.errstatus=errstatus;
    }

    public SubmitOutstandingReportEntity(String datet,String inputUser,String jv,String vendorNo,String invNo,String invoiceCost,String wmCost,String batchId,String poNo,String trans,String rece,String errcode,String errdesc,String errstatus,String invoiceDate,String taxAmount,String taxRate,String taxType){
        this.datet=datet;
        this.inputUser=inputUser;
        this.jv=jv;
        this.vendorNo=vendorNo;
        this.invNo=invNo;
        this.invoiceCost=invoiceCost;
        this.wmCost=wmCost;
        this.batchId=batchId;
        this.poNo=poNo;
        this.trans=trans;
        this.rece=rece;
        this.errcode=errcode;
        this.errdesc=errdesc;
        this.errstatus=errstatus;
        this.invoiceDate=invoiceDate;
        this.taxAmount=taxAmount;
        this.taxRate=taxRate;
        this.taxType=taxType;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDatet() {
        return datet;
    }

    public void setDatet(String datet) {
        this.datet = datet;
    }

    public String getInputUser() {
        return inputUser;
    }

    public void setInputUser(String inputUser) {
        this.inputUser = inputUser;
    }

    public String getJv() {
        return jv;
    }

    public void setJv(String jv) {
        this.jv = jv;
    }

    public String getVendorNo() {
        return vendorNo;
    }

    public void setVendorNo(String vendorNo) {
        this.vendorNo = vendorNo;
    }

    public String getInvNo() {
        return invNo;
    }

    public void setInvNo(String invNo) {
        this.invNo = invNo;
    }

    public String getInvoiceCost() {
        return invoiceCost;
    }

    public void setInvoiceCost(String invoiceCost) {
        this.invoiceCost = invoiceCost;
    }

    public String getWmCost() {
        return wmCost;
    }

    public void setWmCost(String wmCost) {
        this.wmCost = wmCost;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getPoNo() {
        return poNo;
    }

    public void setPoNo(String poNo) {
        this.poNo = poNo;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public String getRece() {
        return rece;
    }

    public void setRece(String rece) {
        this.rece = rece;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrdesc() {
        return errdesc;
    }

    public void setErrdesc(String errdesc) {
        this.errdesc = errdesc;
    }

    public String getErrstatus() {
        return errstatus;
    }

    public void setErrstatus(String errstatus) {
        this.errstatus = errstatus;
    }

}