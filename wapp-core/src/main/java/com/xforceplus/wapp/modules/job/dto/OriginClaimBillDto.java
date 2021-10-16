package com.xforceplus.wapp.modules.job.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xforceplus.wapp.repository.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>
 * 原始索赔单数据
 * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OriginClaimBillDto extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 扣款日期
     */
    @ExcelProperty("扣款日期")
    private String deductionDate;

    /**
     * 扣款日期（Month）
     */
    @ExcelProperty("扣款日期(Month)")
    private String deductionMonth;

    /**
     * 扣款日期（Month Index)
     */
    @ExcelProperty("扣款日期(Month Index)")
    private String deductionMonthIndex;

    /**
     * 扣款公司
     */
    @ExcelProperty("扣款公司")
    private String deductionCompany;

    /**
     * 供应商号
     */
    @ExcelProperty("供应商号")
    private String vendorNo;

    /**
     * 类型
     */
    @ExcelProperty("类型")
    private String type;

    /**
     * 备注
     */
    @ExcelProperty("备注")
    private String remark;

    /**
     * 索赔号/换货号
     */
    @ExcelProperty("索赔号/换货号")
    private String exchangeNo;

    /**
     * 索赔号
     */
    @ExcelProperty("索赔号")
    private String claimNo;

    /**
     * 定案日期
     */
    @ExcelProperty("定案日期")
    private String decisionDate;

    /**
     * 成本金额
     */
    @ExcelProperty("成本金额")
    private String costAmount;

    /**
     * 所扣发票
     */
    @ExcelProperty("所扣发票")
    private String invoiceReference;

    /**
     * 税率
     */
    @ExcelProperty("税率")
    private String taxRate;

    /**
     * 含税金额
     */
    @ExcelProperty("含税金额")
    private String amountWithTax;

    /**
     * 店铺类型（Hyper或Sams）
     */
    @ExcelProperty("店铺类型")
    private String storeType;

}
