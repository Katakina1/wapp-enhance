package com.xforceplus.wapp.modules.InformationInquiry.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 扫描表
 */
public class ScanningExcelEntity extends BaseRowModel implements Serializable {


    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    @ExcelProperty(value={"JV"},index = 1)
    private String jvCode;//对应orgcode
    @ExcelProperty(value={"公司编码"},index = 2)
    private String companyCode;//对应companyCode
    @ExcelProperty(value={"供应商号"},index = 3)
    private String venderid;//供应商号
    @ExcelProperty(value={"购方名称"},index = 4)
    private String gfName;//购方名称
    @ExcelProperty(value={"销方名称"},index = 5)
    private String xfName;//销方名称
    @ExcelProperty(value={"发票号码"},index = 6)
    private String invoiceNo;//发票号
    private String invoiceType;//发票类型
    @ExcelProperty(value={"开票日期"},index = 7)
    private String  invoiceDate;//开票日期
    @ExcelProperty(value={"金额"},index = 8)
    private String  invoiceAmount;//发票金额
    @ExcelProperty(value={"退票原因"},index = 9)
    private String refundNotes;//退单原因
    @ExcelProperty(value={"退单号"},index = 10)
    private String rebateno;//退单号
    @ExcelProperty(value={"邮包号"},index = 11)
    private String rebateExpressno;//邮包号



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

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
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

    public String getRefundNotes() {
        return refundNotes;
    }

    public void setRefundNotes(String refundNotes) {
        this.refundNotes = refundNotes;
    }

    public String getRebateno() {
        return rebateno;
    }

    public void setRebateno(String rebateno) {
        this.rebateno = rebateno;
    }

    public String getRebateExpressno() {
        return rebateExpressno;
    }

    public void setRebateExpressno(String rebateExpressno) {
        this.rebateExpressno = rebateExpressno;
    }



    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }
}
