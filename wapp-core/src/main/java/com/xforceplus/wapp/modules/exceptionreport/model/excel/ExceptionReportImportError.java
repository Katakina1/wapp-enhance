package com.xforceplus.wapp.modules.exceptionreport.model.excel;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import lombok.Getter;
import lombok.Setter;

/**
 *生成错误信息EXCL
 */
@Getter
@Setter
public class ExceptionReportImportError extends BaseRowModel {
    @ExcelProperty(value = "流水号", index = 0)
    private String id;
    @ExcelProperty(value = "备注", index = 1)
    private String errorMsg;

}
