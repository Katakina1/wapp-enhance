package com.xforceplus.wapp.modules.taxcode.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * @Description Riversand税编同步出
 * @Author pengtao
 * @return
 **/
@Data
public class RiversandTcExportDto {

    @ExcelProperty("新增日期")
    @ColumnWidth(20)
    private String createTime;

    @ExcelProperty("商品号")
    @ColumnWidth(25)
    private String itemNo;

    @ExcelProperty("商品描述")
    @ColumnWidth(20)
    private String itemName;

    @ExcelProperty("商品税收编码")
    @ColumnWidth(20)
    private String goodsTaxNo;

    @ExcelProperty("销项税")
    @ColumnWidth(20)
    private String taxRate;

    @ExcelProperty("优惠政策标识")
    @ColumnWidth(20)
    private String taxPre;

    @ExcelProperty("零税率标志")
    @ColumnWidth(20)
    private String zeroTax;

    @ExcelProperty("优惠政策内容")
    @ColumnWidth(20)
    private String taxPreCon;

    @ExcelProperty("同步状态")
    @ColumnWidth(25)
    private String status;

}
