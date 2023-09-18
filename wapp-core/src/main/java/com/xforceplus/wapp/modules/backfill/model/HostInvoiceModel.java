package com.xforceplus.wapp.modules.backfill.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by SunShiyong on 2021/10/21.
 */
@Data
public class HostInvoiceModel {
    @ExcelIgnore
    private Long id;
    @ExcelProperty(value = "供应商号")
    private String venderid;
    @ExcelProperty(value = "供应商名称")
    private String xfName;
    @ExcelProperty(value = "HOST发票号码")
    private String hostInv;
    @ExcelProperty(value = "全电发票号码")
    private String invoiceNo;
    @ExcelProperty(value = "开票日期")
    private String invoiceDate;
    @ExcelProperty(value = "发票金额")
    private BigDecimal invoiceAmount;
    @ExcelProperty(value = "税额")
    private BigDecimal taxAmount;
    @ExcelProperty(value = "价税合计")
    private BigDecimal totalAmount;
    @ExcelProperty(value = "税率（%）")
    String taxRate;
}
