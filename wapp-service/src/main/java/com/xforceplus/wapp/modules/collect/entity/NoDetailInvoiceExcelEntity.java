package com.xforceplus.wapp.modules.collect.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.io.Serializable;

/**
 * 扫描表
 */
public class NoDetailInvoiceExcelEntity extends BaseRowModel implements Serializable {


    @ExcelProperty(value={"发票代码"},index = 0)
    private String invoiceCode;
    @ExcelProperty(value={"发票号码"},index = 1)
    private String invoiceNo;//对应orgcode
    @ExcelProperty(value={"开票日期"},index = 2)
    private String invoiceDate;//对应companyCode
    @ExcelProperty(value={"购方名称"},index = 3)
    private String gfName;//供应商号
    @ExcelProperty(value={"销方名称"},index = 4)
    private String xfName;//购方名称
    @ExcelProperty(value={"发票金额"},index = 5)
    private String invoiceAmount;//销方名称
    @ExcelProperty(value={"税额"},index = 6)
    private String taxAmount;//销方名称
    @ExcelProperty(value={"校验码"},index = 7)
    private String checkCode;//销方名称
    @ExcelProperty(value={"采集时间"},index = 8)
    private String createDate;//销方名称

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

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
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

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
