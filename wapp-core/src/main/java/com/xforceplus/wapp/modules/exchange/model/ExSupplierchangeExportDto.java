package com.xforceplus.wapp.modules.exchange.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.string.StringStringConverter;
import com.xforceplus.wapp.modules.noneBusiness.convert.*;
import lombok.Data;


@Data
public class ExSupplierchangeExportDto {

    @ExcelProperty(value = "发票号码", index = 0)
    private String invoiceNo;
    @ExcelProperty(value = "发票代码", index = 1)
    private String invoiceCode;
    @ExcelProperty(value = "开票日期", index = 2)
    private String paperDrewDate;
    @ExcelProperty(value = "jv", index = 3)
    private String jvcode;
    @ExcelProperty(value = "购方公司", index = 4)
    private String gfName;

    @ExcelProperty(value = "供应商编号", index = 5)
    private String sellerNo;
    @ExcelProperty(value = "销方公司", index = 6)
    private String xfName;
    @ExcelProperty(value = "不含税金额", index = 7)
    private String amountWithTax;
    @ExcelProperty(value = "税率", index = 8)
    private String taxRate;
    @ExcelProperty(value = "税额", index = 9)
    private String taxAmount;
    @ExcelProperty(value = "业务类型", index = 10,converter = BusinessStatusConver.class)
    private String flowType;
    @ExcelProperty(value = "换票状态", index = 11, converter = ExchangeStatusConver.class)
    private String exchangeStatus;


}
