package com.xforceplus.wapp.modules.analysis.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvoiceDataExcelEntity extends BaseRowModel {

    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    @ExcelProperty(value={"供应商号"},index = 1)
    private String vendorid;
    @ExcelProperty(value={"供应商名称"},index = 2)
    private String vendorName;
    @ExcelProperty(value={"提交发票数量"},index = 3)
    private String totalNum;
}
