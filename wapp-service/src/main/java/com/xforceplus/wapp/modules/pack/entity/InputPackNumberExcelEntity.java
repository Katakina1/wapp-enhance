package com.xforceplus.wapp.modules.pack.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

/**
 * 抵账表实体(发票签收)
 */
public class InputPackNumberExcelEntity extends BaseRowModel implements Serializable {


    //序号
    @ExcelProperty(value = {"序号"}, index = 0)
    private String indexNo;

    //装订册号
    @ExcelProperty(value = {"装订册号"}, index = 1)
    private String bbindingNo;
    @ExcelProperty(value = {"eps单号"}, index = 2)
    private String epsNo;
    //扫描流水号
    @ExcelProperty(value = {"扫描流水号"}, index = 3)
    private String invoiceSerialNo;
    //装订时间
    @ExcelProperty(value = {"装订时间"}, index = 4)
    private String bbindingDate;
    @ExcelProperty(value = {"装箱号"}, index = 5)
    private String packingNo;
    @ExcelProperty(value = {"装箱存放地址"}, index = 6)
    private String packingAddress;

    public String getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(String indexNo) {
        this.indexNo = indexNo;
    }

    public String getBbindingNo() {
        return bbindingNo;
    }

    public void setBbindingNo(String bbindingNo) {
        this.bbindingNo = bbindingNo;
    }

    public String getEpsNo() {
        return epsNo;
    }

    public void setEpsNo(String epsNo) {
        this.epsNo = epsNo;
    }

    public String getInvoiceSerialNo() {
        return invoiceSerialNo;
    }

    public void setInvoiceSerialNo(String invoiceSerialNo) {
        this.invoiceSerialNo = invoiceSerialNo;
    }

    public String getBbindingDate() {
        return bbindingDate;
    }

    public String getPackingNo() {
        return packingNo;
    }

    public void setPackingNo(String packingNo) {
        this.packingNo = packingNo;
    }

    public String getPackingAddress() {
        return packingAddress;
    }

    public void setPackingAddress(String packingAddress) {
        this.packingAddress = packingAddress;
    }

    public void setBbindingDate(String bbindingDate) {
        this.bbindingDate = bbindingDate;
    }
}

