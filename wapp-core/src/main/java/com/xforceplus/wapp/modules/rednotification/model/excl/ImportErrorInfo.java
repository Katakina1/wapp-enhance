package com.xforceplus.wapp.modules.rednotification.model.excl;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;

/**
 *生成错误信息EXCL
 */
@Getter
@Setter
public class ImportErrorInfo extends BaseRowModel {
    @ExcelProperty(value = "申请流水号", index = 0)
    private String serialNo;

    @ExcelProperty(value = "错误信息", index = 1)
    @ColumnWidth(100)
    private String errorMsg;

}
