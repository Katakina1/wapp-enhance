package com.xforceplus.wapp.modules.deduct.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xforceplus.wapp.modules.deduct.excelconverter.Converter;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by SunShiyong on 2021/10/22.
 */
@Data
public class ExportClaimBillItemModel {

    @ExcelProperty("索赔单明细号")
    private Long id;

    @ExcelProperty("索赔单号")
    private String businessNo;

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


    @ExcelProperty("规格型号")
    private String itemSpec;

    @ExcelProperty(value = "红字信息表状态", converter = Converter.RedNotificationStatus.class)
    private List<Integer> redNotificationStatus;

    @ExcelProperty(value = "红字信息表编号",converter = Converter.RedNotificationNos.class)
    private List<String> redNotificationNos;


}
