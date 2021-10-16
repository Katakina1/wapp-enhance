package com.xforceplus.wapp.modules.job.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xforceplus.wapp.repository.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>
 * 索赔单Sams明细
 * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OriginClaimItemSamsDto extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("primary_desc")
    private String primaryDesc;

    @ExcelProperty("ITEM_TAX_PCT")
    private String itemTaxPct;

    @ExcelProperty("ship_cost")
    private String shipCost;

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
