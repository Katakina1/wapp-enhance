package com.xforceplus.wapp.modules.InformationInquiry.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.io.Serializable;

public class PaymentInvoiceUploadExcelEntity extends BaseRowModel implements Serializable {


    //序号
    @ExcelProperty(value={"序号"},index = 0)
    private String indexNo;
    //jvcode
    @ExcelProperty(value={"扣款公司"},index = 1)
    private String jvcode;
    //供应商号
    @ExcelProperty(value={"供应商号"},index = 2)
    private String supplierAssociation;
    //类型
    @ExcelProperty(value={"类型"},index = 3)
    private String caseType;
    //备注
    @ExcelProperty(value={"扣款项目"},index = 4)
    private String remark;
    //换货号
    @ExcelProperty(value={"换货号"},index = 5)
    private String exchangeNo;
    //索赔号
    @ExcelProperty(value={"索赔号"},index = 6)
    private String returnGoodsCode;
    //定案日期
    @ExcelProperty(value={"定案日期"},index = 7)
    private String returnGoodsDate;
    //成本金额
    @ExcelProperty(value={"成本金额"},index = 8)
    private  String returnCostAmount;


    //供应商结款发票号
    @ExcelProperty(value={"供应商结款发票号"},index = 9)
    private String paymentInvoiceNo;
    //扣款日期
    @ExcelProperty(value={"扣款日期"},index = 10)
    private String deductionDate;
    //沃尔玛扣款发票号
    @ExcelProperty(value={"沃尔玛扣款发票号"},index = 11)
    private String purchaseInvoiceNo;
    //税率
    @ExcelProperty(value={"税率"},index = 12)
    private String taxRate;
    //含税金额
    @ExcelProperty(value={"含税金额"},index = 13)
    private String taxAmount;
    //发送日期
    @ExcelProperty(value={"上传日期"},index = 14)
    private String sendDate;
    //邮寄时间
    @ExcelProperty(value={"邮寄时间"},index = 15)
    private String mailData;
    //快递号码
    @ExcelProperty(value={"快递单号"},index = 16)
    private String expressNo;
    //快递公司
    @ExcelProperty(value={"快递公司"},index = 17)
    private String expressName;

    public String getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(String indexNo) {
        this.indexNo = indexNo;
    }

    public String getJvcode() {
        return jvcode;
    }

    public void setJvcode(String jvcode) {
        this.jvcode = jvcode;
    }

    public String getSupplierAssociation() {
        return supplierAssociation;
    }

    public void setSupplierAssociation(String supplierAssociation) {
        this.supplierAssociation = supplierAssociation;
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

    public String getPaymentInvoiceNo() {
        return paymentInvoiceNo;
    }

    public void setPaymentInvoiceNo(String paymentInvoiceNo) {
        this.paymentInvoiceNo = paymentInvoiceNo;
    }

    public String getDeductionDate() {
        return deductionDate;
    }

    public void setDeductionDate(String deductionDate) {
        this.deductionDate = deductionDate;
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

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getMailData() {
        return mailData;
    }

    public void setMailData(String mailData) {
        this.mailData = mailData;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }
}
