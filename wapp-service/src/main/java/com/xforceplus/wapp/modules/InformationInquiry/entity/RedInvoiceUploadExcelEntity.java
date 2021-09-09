package com.xforceplus.wapp.modules.InformationInquiry.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.io.Serializable;

public class RedInvoiceUploadExcelEntity extends BaseRowModel implements Serializable {


    //序号
    @ExcelProperty(value={"序号"},index = 0)
    private String indexNo;
    //供应商号
    @ExcelProperty(value={"供应商号"},index = 3)
    private String venderid;
    //成本金额
    @ExcelProperty(value={"金额(含税)"},index = 5)
    private String redAmount;
    /*//税率
    @ExcelProperty(value={"税率"},index = 6)
    private String taxRate;
    //税额
    @ExcelProperty(value={"税额"},index =7)
    private String taxAmount;*/
    //venderName
    @ExcelProperty(value={"供应商名称"},index = 4)
    private String venderName;
    //发票或协议号
    @ExcelProperty(value={"发票或协议号"},index = 6)
    private String invoiceOrAgreementNo;
    //红票信息编号
    @ExcelProperty(value={"红票信息编号"},index = 7)
    private String redInvoiceNo;
    //红票信息生成时间
    @ExcelProperty(value={"红票信息生成日期"},index = 2)
    private String redInvoiceDate;
    //红票信息生成时间
    @ExcelProperty(value={"类型"},index = 1)
    private String redType;
    //上传时间
    private String uploadDate;

    public String getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(String indexNo) {
        this.indexNo = indexNo;
    }

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }

    public String getRedAmount() {
        return redAmount;
    }

    public void setRedAmount(String redAmount) {
        this.redAmount = redAmount;
    }

   /* public String getTaxRate() {
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
    }*/

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
