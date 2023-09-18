package com.xforceplus.wapp.modules.discountRateSetting.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * @Description 供应商限额导出
 * @Author pengtao
 * @return
 **/
@Data
public class OrgExportDto {

    @ExcelProperty("供应商名称")
    @ColumnWidth(25)
    private String orgName;

    @ExcelProperty("供应商税号")
    @ColumnWidth(25)
    private String orgCode;

    @ExcelProperty("专票限额")
    @ColumnWidth(25)
    private Double quota;

}
