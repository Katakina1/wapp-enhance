package com.xforceplus.wapp.modules.customs.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * @Description 海关票导出（设计变更不再使用）
 * @Author pengtao
 * @return
**/
@Data
public class CustomsExport1Dto {

    @ExcelProperty("是否勾选")
    @ColumnWidth(15)
    private String isClick;

    @ExcelProperty("缴款书号码")
    @ColumnWidth(25)
    private String customsNo;

    @ExcelProperty("填发日期")
    @ColumnWidth(20)
    private String paperDrewDate;

    @ExcelProperty("税款金额")
    @ColumnWidth(20)
    private String taxAmount;

    @ExcelProperty("有效抵扣税款金额")
    @ColumnWidth(25)
    private String effectiveTaxAmount;

    @ExcelProperty("用途")
    @ColumnWidth(20)
    private String checkPurpose;
}
