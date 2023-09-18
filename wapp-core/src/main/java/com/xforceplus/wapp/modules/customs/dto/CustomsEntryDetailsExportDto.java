package com.xforceplus.wapp.modules.customs.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * @Description 海关票入账明细导出
 * @Author pengtao
 * @return
 **/
@Data
public class CustomsEntryDetailsExportDto {

    @ExcelProperty("海关缴款书号码")
    @ColumnWidth(25)
    private String customsNo;

    @ExcelProperty("PO号")
    @ColumnWidth(20)
    private String contractNo;

    @ExcelProperty("报关单编号")
    @ColumnWidth(20)
    private String customsDocNo;

    @ExcelProperty("税号")
    @ColumnWidth(25)
    private String companyTaxNo;

    @ExcelProperty("货物名称")
    @ColumnWidth(25)
    private String materialDesc;

    @ExcelProperty("完税价格")
    @ColumnWidth(20)
    private String dutiablePrice;

    @ExcelProperty("税款金额")
    @ColumnWidth(25)
    private String taxAmount;

    @ExcelProperty("税率")
    @ColumnWidth(10)
    private String taxRate;

}
