package com.xforceplus.wapp.modules.rednotification.model.excl;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ExportInfo extends BaseRowModel {
    @ExcelProperty(value = "购方名称", index = 0)
    private String purchaserName;

    @ExcelProperty(value = "销方名称", index = 1)
    private String sellerName;

    @ExcelProperty(value = "业务单号", index = 2)
    private String billNo;

    @ExcelProperty(value = "申请流水号", index = 3)
    private String sellerNumber;

    @ExcelProperty(value = "发票代码", index = 4)
    private String invoiceCode;

    @ExcelProperty(value = "发票号码", index = 5)
    private String invoiceNo;

    @ExcelProperty(value = "合计金额", index = 6)
    private String amountWithoutTax;

    @ExcelProperty(value = "合计税额", index = 7)
    private String taxAmount;

    @ExcelProperty(value = "价税合计", index = 8)
    private String amountWithTax;

    @ExcelProperty(value = "红字信息表编号", index = 9)
    private String applyTaxNo;

    @ExcelProperty(value = "申请结果", index = 10)
    private String applyStatus;

    @ExcelProperty(value = "发票类型", index = 11)
    private String invoiceType;

    @ExcelProperty(value = "原发票类型", index = 12)
    private String originInvoiceType;

}
