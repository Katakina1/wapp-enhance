package com.xforceplus.wapp.modules.posuopei.entity;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.io.Serializable;

/**
 * @author raymond.yan
 */
public class DetailInvExcelEntity extends BaseRowModel implements Serializable {




    //序号
    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    //购方税号
    @ExcelProperty(value={"发票代码"},index = 1)
    private  String invoiceCode;
    @ExcelProperty(value={"发票号码"},index = 2)
    private  String invoiceNo;
    //供应商名称
    @ExcelProperty(value={"发票金额"},index = 3)
    private  String invoiceAmount;
    //发票金额合计(结算金额)
    @ExcelProperty(value={"发票税额"},index = 4)
    private String invoiceTaxAmount;
    //发票数量
    @ExcelProperty(value={"价税合计"},index = 5)
    private String invoiceTotal;
    //po 金额合计
    @ExcelProperty(value={"税率"},index = 6)
    private String taxTate;
    @ExcelProperty(value={"供应商号"},index = 7)
    private String venderId;
    //claim金额合计
    @ExcelProperty(value={"供应商名称"},index = 8)
    private String venderName;
    @ExcelProperty(value={"开票日期"},index = 9)
    private String createDate;
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

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceTaxAmount() {
        return invoiceTaxAmount;
    }

    public void setInvoiceTaxAmount(String invoiceTaxAmount) {
        this.invoiceTaxAmount = invoiceTaxAmount;
    }

    public String getInvoiceTotal() {
        return invoiceTotal;
    }

    public void setInvoiceTotal(String invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
    }

    public String getTaxTate() {
        return taxTate;
    }

    public void setTaxTate(String taxTate) {
        this.taxTate = taxTate;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

}
