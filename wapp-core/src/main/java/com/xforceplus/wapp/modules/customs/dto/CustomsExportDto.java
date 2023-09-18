package com.xforceplus.wapp.modules.customs.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 海关票导出
 * @Author pengtao
 * @return
 **/
@Data
public class CustomsExportDto {

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

    @ExcelProperty("勾选日期")
    @ColumnWidth(20)
    private String checkTime;

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
    @ColumnWidth(25)
    private String manageStatus;

    @ExcelProperty("税款所属期")
    @ColumnWidth(20)
    private String taxPeriod;

    @ExcelProperty("用途")
    @ColumnWidth(20)
    private String checkPurpose;

    @ExcelProperty("撤销勾选日期")
    @ColumnWidth(20)
    private String unCheckTime;

    @ExcelProperty("勾选失败原因")
    @ColumnWidth(20)
    private String authRemark;

/*    @ExcelProperty("PO号")
    @ColumnWidth(20)
    private String contractNo;

    @ExcelProperty("报关单编号(从BMS获取返回)")
    @ColumnWidth(20)
    private String customsDocNo;*/

}
