package com.xforceplus.wapp.modules.job.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.string.StringStringConverter;
import com.xforceplus.wapp.modules.noneBusiness.convert.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Data
public class HostUnMatchInvoiceExportDto {

    @ExcelProperty(value = "供应商号", index = 0)
    private String venderid;
    @ExcelProperty(value = "发票代码", index = 1)
    private String invoiceCode;
    @ExcelProperty(value = "发票号码", index = 2)
    private String invoiceNo;
    @ExcelProperty(value = "开票日期", index = 3)
    private Date invoiceDate;
    @ExcelProperty(value = "发票金额", index = 4)
    private BigDecimal invoiceAmount;
    @ExcelProperty(value = "税额", index = 5)
    private BigDecimal taxAmount;



}
