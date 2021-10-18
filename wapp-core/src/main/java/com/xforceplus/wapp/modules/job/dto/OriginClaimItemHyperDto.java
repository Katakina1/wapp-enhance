package com.xforceplus.wapp.modules.job.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xforceplus.wapp.repository.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>
 * 索赔单Hyper明细
 * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OriginClaimItemHyperDto extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("UPC_NBR")
    private String upcNbr;

    @ExcelProperty("UNIT_COST")
    private String unitCost;

    @ExcelProperty("VENDOR_STOCK_ID")
    private String vendorStockId;

    @ExcelProperty("CLAIM_NBR")
    private String claimNbr;

    @ExcelProperty("VNDR_NBR")
    private String vndrNbr;

    @ExcelProperty("DEPT_NBR")
    private String deptNbr;

    @ExcelProperty("TAX_RATE")
    private String taxRate;

    @ExcelProperty("FINAL_DATE")
    private String finalDate;

    @ExcelProperty("CATEGORY_NBR")
    private String categoryNbr;

    @ExcelProperty("VNPK_COST")
    private String vnpkCost;

    @ExcelProperty("ITEM_QTY")
    private String itemQty;

    @ExcelProperty("LINE_COST")
    private String lineCost;

    @ExcelProperty("VNPK_QTY")
    private String vnpkQty;

    @ExcelProperty("CN_DESC")
    private String cnDesc;

    @ExcelProperty("ITEM_NBR")
    private String itemNbr;

}