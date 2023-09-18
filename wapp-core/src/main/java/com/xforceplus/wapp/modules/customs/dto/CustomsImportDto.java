package com.xforceplus.wapp.modules.customs.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("海关缴款书导入")
public class CustomsImportDto {

    @ExcelProperty(value = "海关缴款书号", index = 0)
    @ColumnWidth(25)
    private String customsNo;

    @ExcelProperty(value = "凭证号", index = 1)
    @ColumnWidth(25)
    private String voucherNo;

    @ExcelProperty(value = "凭证入账日期(yyyy-MM-dd)", index = 2)
    @ColumnWidth(25)
    private String voucherAccountTime;

    @ExcelProperty(value = "错误信息", index = 3)
    @ColumnWidth(50)
    private String errorMsg;
}
