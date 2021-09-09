package com.xforceplus.wapp.modules.lease.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class InvoiceImportAndExportExcelEntity extends BaseRowModel {

    @ExcelProperty(value={"发票表ID"},index = 0)
    private String  id;
    @ExcelProperty(value={"序号"},index = 1)
    private String  rownumber;

    @ExcelProperty(value={"公司代码"},index = 5)
    private String  companyCode;
    @ExcelProperty(value={"发票代码"},index = 7)
    private String  invoiceCode;
    @ExcelProperty(value={"发票类型"},index = 2)
    private String  invoiceType;
    @ExcelProperty(value={"发票号码"},index = 8)
    private String  invoiceNo;
    @ExcelProperty(value={"供应商编码"},index = 4)
    private String  venderId;
    @ExcelProperty(value={"供应商名称"},index = 6)
    private String  venderName;
    @ExcelProperty(value={"发票税额"},index = 11)
    private String  taxAmount;
    @ExcelProperty(value={"税率"},index = 12)
    private String  taxRate;
    @ExcelProperty(value={"发票金额"},index = 10)
    private String  invoiceAmount;
    @ExcelProperty(value={"价税合计"},index = 13)
    private String  totalAmount;
    @ExcelProperty(value={"开票日期"},index = 9)
    private String    invoiceDate;
    @ExcelProperty(value={"备注"},index = 14)
    private String  reMark;
    @ExcelProperty(value={"JV"},index = 3)
    private String  jvCode;
    @ExcelProperty(value={"店号"},index = 15)
    private String  shopNo;
    @ExcelProperty(value={"期间"},index = 16)
    private String  peRiod;
    @ExcelProperty(value={"匹配状态"},index = 17)
    private String  matChing;
    @ExcelProperty(value={"匹配时间"},index = 18)
    private String  matChingDate;
    @ExcelProperty(value={"税码"},index = 19)
    private String  taxCode;

}
