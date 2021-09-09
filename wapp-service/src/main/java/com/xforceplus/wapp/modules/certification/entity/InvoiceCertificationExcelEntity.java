package com.xforceplus.wapp.modules.certification.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * 发票认证实体导出类
 * @author fth
 * @date 07/01/2019
 */

public class InvoiceCertificationExcelEntity extends BaseRowModel {
    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    @ExcelProperty(value={"发票代码"},index = 1)
    private String invoiceCode;
    @ExcelProperty(value={"发票号码"},index = 2)
    private String invoiceNo;
    @ExcelProperty(value={"开票日期"},index = 3)
    private String invoiceDate;
    @ExcelProperty(value={"金额"},index = 4)
    private String invoiceAmount;
    @ExcelProperty(value={"税额"},index = 5)
    private String taxAmount;
    @ExcelProperty(value={"价税合计"},index = 6)
    private String totalAmount;
    @ExcelProperty(value={"供应商号"},index = 7)
    private String venderid;
    @ExcelProperty(value={"凭证号"},index = 8)
    private String certificateNo;
    @ExcelProperty(value={"JV"},index = 9)
    private String jvcode;
    @ExcelProperty(value={"公司代码"},index = 10)
    private String companyCode;
    @ExcelProperty(value={"业务类型"},index = 11)
    private String flowType;


    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
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

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
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

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }

    public String getCertificateNo() {
        return certificateNo;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public String getJvcode() {
        return jvcode;
    }

    public void setJvcode(String jvcode) {
        this.jvcode = jvcode;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }
}
