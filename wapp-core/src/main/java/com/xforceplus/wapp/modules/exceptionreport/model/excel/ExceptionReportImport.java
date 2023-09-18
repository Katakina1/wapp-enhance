package com.xforceplus.wapp.modules.exceptionreport.model.excel;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import lombok.Data;

// 例外报告导入
@Data
public class ExceptionReportImport extends BaseRowModel {
    @ExcelProperty(value = "流水号", index = 0)
    private String id;
    @ExcelProperty(value = "备注", index = 13)
    private String remark;

}
