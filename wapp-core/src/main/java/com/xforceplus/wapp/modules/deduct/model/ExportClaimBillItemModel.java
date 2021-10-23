package com.xforceplus.wapp.modules.deduct.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by SunShiyong on 2021/10/22.
 */
@Data
public class ExportClaimBillItemModel {
    @ExcelProperty("索赔单明细号")
    private Long id;

    @ExcelProperty("商品编码")
    private String itemNo;

    @ExcelProperty("商品名称")
    private String cnDesc;

    @ExcelProperty("税收分类编码")
    private String goodsTaxNo;

    @ExcelProperty("数量")
    private BigDecimal quantity;

    @ExcelProperty("单价")
    private BigDecimal price;

    @ExcelProperty("不含税金额")
    private BigDecimal amountWithoutTax;

    @ExcelProperty("税率")
    private BigDecimal taxRate;

    @ExcelProperty("税额")
    private BigDecimal taxAmount;

    @ExcelProperty("单位")
    private String unit;
}
