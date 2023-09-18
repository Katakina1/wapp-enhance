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

    @ExcelProperty(value = "结算单号", index = 2)
    private String billNo;

    @ExcelProperty(value = "申请流水号", index = 3)
    private String serialNo;

    @ExcelProperty(value = "发票代码", index = 4)
    private String originInvoiceCode;

    @ExcelProperty(value = "发票号码", index = 5)
    private String originInvoiceNo;

    @ExcelProperty(value = "合计金额", index = 6)
    private String amountWithoutTax;

    @ExcelProperty(value = "合计税额", index = 7)
    private String taxAmount;

    @ExcelProperty(value = "价税合计", index = 8)
    private String amountWithTax;

    @ExcelProperty(value = "红字信息表编号", index = 9)
    private String redNotificationNo;

    @ExcelProperty(value = "申请结果", index = 10)
    private String approveStatus;

    @ExcelProperty(value = "发票类型", index = 11)
    private String invoiceType;

    @ExcelProperty(value = "原发票类型", index = 12)
    private String originInvoiceType;

    @ExcelProperty(value = "销方税号", index = 13)
    private String sellerTaxNo;

    @ExcelProperty(value = "购方税号", index = 14)
    private String purchaserTaxNo;

    @ExcelProperty(value = "申请时间", index = 15)
    private String createDate;

    @ExcelProperty(value = "申请成功日期", index = 16)
    private String invoiceDate;

    @ExcelProperty(value = "撤销时间", index = 17)
    private String cancelTime;

    @ExcelProperty(value = "业务类型", index = 18)
    private String invoiceOrigin;

    @ExcelProperty(value = "失败原因", index = 19)
    private String applyRemark;

    @ExcelProperty(value = "撤销原因", index = 20)
    private String applyReason;

}
