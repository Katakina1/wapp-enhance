package com.xforceplus.wapp.modules.report.entity;

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
public class InvoiceQuestionQueryExcelEntity extends BaseRowModel implements Serializable {

    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    //供应商号
    @ExcelProperty(value={"供应商号"},index = 1)
    private String venderId;
    //供应商号
    @ExcelProperty(value={"供应商名字"},index = 2)
    private String venderName;

    //发票号码
    @ExcelProperty(value={"正常发票数量"},index = 3)
    private String normalInvoice;

    //开票日期
    @ExcelProperty(value={"问题发票数量"},index = 4)
    private String problemInvoice;
    //金额
    @ExcelProperty(value={"问题发票比率"},index = 5)
    private String problemInvoiceRatio;

}
