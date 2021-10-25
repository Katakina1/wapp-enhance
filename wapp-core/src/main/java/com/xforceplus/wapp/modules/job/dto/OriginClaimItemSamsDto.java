package com.xforceplus.wapp.modules.job.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 索赔单Sams明细
 *
 * @author Kenny Wong
 * @since 2021-10-15
 */
@Data
public class OriginClaimItemSamsDto {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("primary_desc")
    private String primaryDesc;

    @ExcelProperty("ITEM_TAX_PCT")
    private String itemTaxPct;

    @ExcelProperty("ship_cost")
    private BigDecimal shipCost;

    @ExcelProperty("item_nbr")
    private String itemNbr;

    @ExcelProperty("rtn_date")
    private String rtnDate;

    @ExcelProperty("ship_retail")
    private String shipRetail;

    @ExcelProperty("dept_nbr")
    private String deptNbr;

    @ExcelProperty("claim_number")
    private String claimNumber;

    @ExcelProperty("vendor_number")
    private String vendorNumber;

    @ExcelProperty("STORE_NBR")
    private String storeNbr;

    @ExcelProperty("unit")
    private String unit;

    @ExcelProperty("vendor_tax_id_chc")
    private String vendorTaxIdChc;

    @ExcelProperty("vendor_name")
    private String vendorName;

    @ExcelProperty("vendor_tax_id_jv")
    private String vendorTaxIdJv;

    @ExcelProperty("reportcode")
    private String reportCode;

    @ExcelProperty("SHIP_QTY")
    private String shipQty;

    @ExcelProperty("OLD_ITEM")
    private String oldItem;

}
