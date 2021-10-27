package com.xforceplus.wapp.modules.job.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 索赔单Hyper明细
 *
 * @author Kenny Wong
 * @since 2021-10-15
 */
@Data
public class OriginClaimItemHyperDto {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("UPC_NBR")
    @Length(max = 20)
    private String upcNbr;

    @ExcelProperty("UNIT_COST")
    @Length(max = 50)
    private String unitCost;

    @ExcelProperty("VENDOR_STOCK_ID")
    @Length(max = 20)
    private String vendorStockId;

    @ExcelProperty("CLAIM_NBR")
    @Length(max = 20)
    private String claimNbr;

    @ExcelProperty("VNDR_NBR")
    @Length(max = 20)
    private String vndrNbr;

    @ExcelProperty("DEPT_NBR")
    @Length(max = 20)
    private String deptNbr;

    @ExcelProperty("TAX_RATE")
    @Length(max = 10)
    private String taxRate;

    @ExcelProperty("FINAL_DATE")
    @Length(max = 20)
    private String finalDate;

    @ExcelProperty("CATEGORY_NBR")
    @Length(max = 20)
    private String categoryNbr;

    @ExcelProperty("VNPK_COST")
    @Length(max = 50)
    private String vnpkCost;

    @ExcelProperty("ITEM_QTY")
    @Length(max = 20)
    private String itemQty;

    @ExcelProperty("LINE_COST")
    @Length(max = 50)
    private String lineCost;

    @ExcelProperty("VNPK_QTY")
    @Length(max = 20)
    private String vnpkQty;

    @ExcelProperty("CN_DESC")
    private String cnDesc;

    @ExcelProperty("ITEM_NBR")
    @Length(max = 20)
    private String itemNbr;

    @ExcelProperty("STORE_NBR")
    @Length(max = 20)
    private String storeNbr;
}
