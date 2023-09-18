package com.xforceplus.wapp.modules.customs.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * @Description 异常海关票导出
 * @Author pengtao
 * @return
 **/
@Data
public class CustomsExport4Dto {

    @ExcelProperty("海关缴款书号码")
    @ColumnWidth(25)
    private String customsNo;

    @ExcelProperty("进口单位名称")
    @ColumnWidth(25)
    private String companyName;

    @ExcelProperty("进口单位税号")
    @ColumnWidth(25)
    private String companyTaxNo;

    @ExcelProperty("填发日期")
    @ColumnWidth(20)
    private String paperDrewDate;

    @ExcelProperty("税款金额")
    @ColumnWidth(20)
    private String taxAmount;

    @ExcelProperty("有效抵扣税款金额")
    @ColumnWidth(25)
    private String effectiveTaxAmount;

    @ExcelProperty("勾选状态")
    @ColumnWidth(25)
    private String isCheck;

    @ExcelProperty("海关缴款书状态")
    @ColumnWidth(20)
    private String manageStatus;

    @ExcelProperty("海关缴款书状态异常原因")
    @ColumnWidth(20)
    private String abnormalInfo;
}
