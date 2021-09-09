package com.xforceplus.wapp.modules.InformationInquiry.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单表
 */
public class QuestionnaireOrLeadEntity extends AbstractBaseDomain {

    private static final long serialVersionUID = 1L;


    private int ids;
    private String dateT;//日期
    private String inputUser;//
    private String jV;//
    private String vendorNo;//
    private String invNo;//
    private String invoiceCost;//
    private String wMCost;//
    private String batchID;//
    private String pONo;//
    private String trans;//
    private String rece;//
    private String errCode;//
    private String errDesc;//
    private String errStatus;//
    private Boolean atrue;
    private  String isDel;
    private  String isDelB;
    private Date invoiceDate;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getIds() {


        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getDateT() {
        return dateT;
    }

    public void setDateT(String dateT) {
        this.dateT = dateT;
    }

    public String getInputUser() {
        return inputUser;
    }

    public void setInputUser(String inputUser) {
        this.inputUser = inputUser;
    }

    public String getjV() {
        return jV;
    }

    public void setjV(String jV) {
        this.jV = jV;
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

    public String getwMCost() {
        return wMCost;
    }

    public void setwMCost(String wMCost) {
        this.wMCost = wMCost;
    }

    public String getBatchID() {
        return batchID;
    }

    public void setBatchID(String batchID) {
        this.batchID = batchID;
    }

    public String getpONo() {
        return pONo;
    }

    public void setpONo(String pONo) {
        this.pONo = pONo;
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

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrDesc() {
        return errDesc;
    }

    public void setErrDesc(String errDesc) {
        this.errDesc = errDesc;
    }

    public String getErrStatus() {
        return errStatus;
    }

    public void setErrStatus(String errStatus) {
        this.errStatus = errStatus;
    }

    public Boolean getAtrue() {
        return atrue;
    }

    public void setAtrue(Boolean atrue) {
        this.atrue = atrue;
    }

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }

    public String getIsDelB() {
        return isDelB;
    }

    public void setIsDelB(String isDelB) {
        this.isDelB = isDelB;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
