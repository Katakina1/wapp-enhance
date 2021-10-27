package com.xforceplus.wapp.modules.job.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

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
    @Length(max = 20)
    private String itemTaxPct;

    @ExcelProperty("ship_cost")
    @Length(max = 20)
    private String shipCost;

    @ExcelProperty("item_nbr")
    @Length(max = 20)
    private String itemNbr;

    @ExcelProperty("rtn_date")
    @Length(max = 20)
    private String rtnDate;

    @ExcelProperty("ship_retail")
    @Length(max = 20)
    private String shipRetail;

    @ExcelProperty("dept_nbr")
    @Length(max = 20)
    private String deptNbr;

    @ExcelProperty("claim_number")
    @Length(max = 20)
    private String claimNumber;

    @ExcelProperty("vendor_number")
    @Length(max = 20)
    private String vendorNumber;

    @ExcelProperty("STORE_NBR")
    @Length(max = 20)
    private String storeNbr;

    @ExcelProperty("unit")
    @Length(max = 10)
    private String unit;

    @ExcelProperty("vendor_tax_id_chc")
    @Length(max = 20)
    private String vendorTaxIdChc;

    @ExcelProperty("vendor_name")
    @Length(max = 50)
    private String vendorName;

    @ExcelProperty("vendor_tax_id_jv")
    @Length(max = 20)
    private String vendorTaxIdJv;

    @ExcelProperty("reportcode")
    @Length(max = 20)
    private String reportCode;

    @ExcelProperty("SHIP_QTY")
    @Length(max = 20)
    private String shipQty;

    @ExcelProperty("OLD_ITEM")
    @Length(max = 20)
    private String oldItem;

}
