package com.xforceplus.wapp.modules.redInvoiceManager.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 费用扫描签收
 */
@Getter
@Setter
public class UploadScarleQueryExcelEntity extends BaseRowModel implements Serializable {

    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    //供应商号
    @ExcelProperty(value={"税务承担店号"},index = 1)
    private String store;
    //供应商号
    @ExcelProperty(value={"收票方名称"},index = 2)
    private String BuyerName;

    //发票号码
    @ExcelProperty(value={"发票类型"},index = 3)
    private String invoiceType;

    //开票日期
    @ExcelProperty(value={"开红票金额"},index = 4)
    private String invoiceAmount;
    //金额
    @ExcelProperty(value={"开红票税率"},index = 5)
    private String taxRate;
    //金额
    @ExcelProperty(value={"开红票税额"},index = 6)
    private String taxAmount;
    //金额
    @ExcelProperty(value={"开票月份"},index = 7)
    private String makeoutDate;    //金额
    @ExcelProperty(value={"序列号"},index = 8)
    private String SerialNumber;    //金额
    @ExcelProperty(value={"jv"},index = 9)
    private String jvcode;
    @ExcelProperty(value={"红字通知单号"},index = 10)
    private String redLetterNotice;
}
