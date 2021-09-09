package com.xforceplus.wapp.modules.InformationInquiry.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PaymentInvoiceUploadEntity implements Serializable {
    //ID
    private Long id;
    //扣款公司
    private String gfName;
    //供应商号
    private String supplierAssociation;
    //类型
    private String caseType;
    //备注
    private String remark;
    //换货号
    private String exchangeNo;
    //索赔号
    private String returnGoodsCode;
    //定案日期
    private String returnGoodsDate;
    //成本金额
    private  String returnCostAmount;
    //供应商结款发票号
    private String paymentInvoiceNo;
    //沃尔玛扣款发票号
    private String purchaseInvoiceNo;
    //税率
    private String taxRate;
    //含税金额
    private String taxAmount;
//    //协议号
//    private String serviceNo;
//    //供应商名称
//    private String venderName;
    //扣款日期
    private String deductionDate;
    //发送日期
    private String sendDate;
    //快递号码
    private String expressNo;
    //邮寄时间
    private String mailData;
    //快递公司
    private String expressName;
    //红票序列号
    private String redticketDataSerialNumber;
    //jvcode
    private String jvcode;
    //序号
    private int indexNo;
    //失败原因
    private String failReason;
    //失败问题发票上传时间
    private String uploadDate;
    //创建人
    private String createByName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGfName() {
        return gfName;
    }

    public void setGfName(String gfName) {
        this.gfName = gfName;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getExchangeNo() {
        return exchangeNo;
    }

    public void setExchangeNo(String exchangeNo) {
        this.exchangeNo = exchangeNo;
    }

    public String getPaymentInvoiceNo() {
        return paymentInvoiceNo;
    }

    public void setPaymentInvoiceNo(String paymentInvoiceNo) {
        this.paymentInvoiceNo = paymentInvoiceNo;
    }

    public String getPurchaseInvoiceNo() {
        return purchaseInvoiceNo;
    }

    public void setPurchaseInvoiceNo(String purchaseInvoiceNo) {
        this.purchaseInvoiceNo = purchaseInvoiceNo;
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

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public String getMailData() {
        return mailData;
    }

    public void setMailData(String mailData) {
        this.mailData = mailData;
    }

    public String getSupplierAssociation() {
        return supplierAssociation;
    }

    public void setSupplierAssociation(String supplierAssociation) {
        this.supplierAssociation = supplierAssociation;
    }

    public String getReturnGoodsCode() {
        return returnGoodsCode;
    }

    public void setReturnGoodsCode(String returnGoodsCode) {
        this.returnGoodsCode = returnGoodsCode;
    }

    public String getReturnGoodsDate() {
        return returnGoodsDate;
    }

    public void setReturnGoodsDate(String returnGoodsDate) {
        this.returnGoodsDate = returnGoodsDate;
    }

    public String getReturnCostAmount() {
        return returnCostAmount;
    }

    public void setReturnCostAmount(String returnCostAmount) {
        this.returnCostAmount = returnCostAmount;
    }

    public String getDeductionDate() {
        return deductionDate;
    }

    public void setDeductionDate(String deductionDate) {
        this.deductionDate = deductionDate;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public String getRedticketDataSerialNumber() {
        return redticketDataSerialNumber;
    }

    public void setRedticketDataSerialNumber(String redticketDataSerialNumber) {
        this.redticketDataSerialNumber = redticketDataSerialNumber;
    }

    public String getJvcode() {
        return jvcode;
    }

    public void setJvcode(String jvcode) {
        this.jvcode = jvcode;
    }

    public int getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(int indexNo) {
        this.indexNo = indexNo;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
    }
}
