package com.xforceplus.wapp.modules.rednotification.model.excl;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExportItemInfo extends BaseRowModel {
    @ExcelProperty(value = "业务单号", index = 0)
    private String billNo;

    @ExcelProperty(value = "申请流水号", index = 1)
    private String sellerNumber;

    @ExcelProperty(value = "货物名称", index = 2)
    private String cargoName;

    @ExcelProperty(value = "规格型号", index = 3)
    private String itemSpec;

    @ExcelProperty(value = "数量单位", index = 4)
    private String quantityUnit;

    @ExcelProperty(value = "含税金额", index = 5)
    private String amountWithTax;

    @ExcelProperty(value = "不含税金额", index = 6)
    private String amountWithoutTax;

    @ExcelProperty(value = "税额", index = 7)
    private String taxAmount;

    @ExcelProperty(value = "数量", index = 8)
    private String quantity;

    @ExcelProperty(value = "税率", index = 9)
    private String taxRate;

    @ExcelProperty(value = "不含税单价", index = 8)
    private String unitPrice;
}
