package com.xforceplus.wapp.modules.customs.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * @Description 海关票入账导出
 * @Author pengtao
 * @return
 **/
@Data
public class CustomsEntryExportDto {

    @ExcelProperty("海关缴款书号码")
    @ColumnWidth(25)
    private String customsNo;

    @ExcelProperty("PO号")
    @ColumnWidth(20)
    private String contractNo;

    @ExcelProperty("报关单编号")
    @ColumnWidth(20)
    private String customsDocNo;

    @ExcelProperty("进口单位名称")
    @ColumnWidth(25)
    private String companyName;

    @ExcelProperty("填发日期")
    @ColumnWidth(20)
    private String paperDrewDate;

    @ExcelProperty("税款金额")
    @ColumnWidth(20)
    private String taxAmount;

    @ExcelProperty("有效抵扣税款金额")
    @ColumnWidth(25)
    private String effectiveTaxAmount;

    @ExcelProperty("税款凭证号")
    @ColumnWidth(25)
    private String voucherNo;

    @ExcelProperty("凭证入账时间")
    @ColumnWidth(25)
    private String voucherAccountTime;

    @ExcelProperty("国税入账状态")
    @ColumnWidth(25)
    private String accountStatus;

    @ExcelProperty("海关缴款书状态")
    @ColumnWidth(25)
    private String manageStatus;

    @ExcelProperty("对比状态")
    @ColumnWidth(20)
    private String billStatus;

    @ExcelProperty("勾选状态")
    @ColumnWidth(25)
    private String isCheck;

    @ExcelProperty("勾选日期")
    @ColumnWidth(25)
    private String checkTime;

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
    @ColumnWidth(25)
    private String authRemark;

    @ExcelProperty("税额差")
    @ColumnWidth(25)
    private String taxAmountDifference;

    @ExcelProperty("进口单位税号")
    @ColumnWidth(25)
    private String companyTaxNo;

    @ExcelProperty("海关缴款书状态异常原因")
    @ColumnWidth(30)
    private String abnormalInfo;
}
