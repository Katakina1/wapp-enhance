package com.xforceplus.wapp.modules.InformationInquiry.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

/**
 * 订单表
 */
public class QuestionnaireEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    private long id;
    //UUID
    private String uuid;
    private Date dateT;//日期
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
    private  String isDel;
    private  String isDelB;
    private Date invoiceDate;
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

    private Long[] ids;
   // private Boolean atrue;
    private  String rownumber;
    private  String delDate;

    public String getDelDate() {
        return delDate;
    }

    public void setDelDate(String delDate) {
        this.delDate = delDate;
    }

    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
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

    public String getTaxType() {
        if("01".equals(taxType)){
            return "专";
        }else if("04".equals(taxType)){
            return "普";
        }else{
            return "";
        }
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDateT() {
        return dateT;
    }

    public void setDateT(Date dateT) {
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public Long[] getIds() {
        return (ids == null) ? null : Arrays.copyOf(ids, ids.length);
    }

    public void setIds(Long[] ids) {
        this.ids = ids == null ? null : Arrays.copyOf(ids, ids.length);
    }

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getIsDelB() {
        return isDelB;
    }

    public void setIsDelB(String isDelB) {
        this.isDelB = isDelB;
    }
}
