package com.xforceplus.wapp.modules.job.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 原始索赔单数据
 *
 * @author Kenny Wong
 * @since 2021-10-15
 */
@Data
public class OriginClaimBillDto {

    private static final long serialVersionUID = 1L;

    /**
     * 报告日期
     */
    @ExcelProperty("报告日期")
    @Length(max = 20)
    private String createTime;
    /**
     * 扣款日期
     */
    @ExcelProperty("扣款日期")
    @Length(max = 20)
    private String deductionDate;

    /**
     * 扣款日期（Month）
     */
    @ExcelProperty("扣款日期(Month)")
    @Length(max = 10)
    private String deductionMonth;

    /**
     * 扣款日期（Month Index)
     */
    @ExcelProperty("扣款日期(Month Index)")
    @Length(max = 10)
    private String deductionMonthIndex;

    /**
     * 扣款公司
     */
    @ExcelProperty("扣款公司")
    @Length(max = 20)
    private String deductionCompany;

    /**
     * 供应商号
     */
    @ExcelProperty("供应商号")
    @Length(max = 20)
    private String vendorNo;

    /**
     * 类型
     */
    @ExcelProperty("类型")
    @Length(max = 20)
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
    @Length(max = 20)
    private String exchangeNo;

    /**
     * 索赔号
     */
    @ExcelProperty("索赔号")
    @Length(max = 20)
    private String claimNo;

    /**
     * 定案日期
     */
    @ExcelProperty("定案日期")
    @Length(max = 20)
    private String decisionDate;

    /**
     * 成本金额
     */
    @ExcelProperty("成本金额")
    @Length(max = 50)
    private String costAmount;

    /**
     * 所扣发票
     */
    @ExcelProperty("所扣发票")
    @Length(max = 20)
    private String invoiceReference;

    /**
     * 税率
     */
    @ExcelProperty("税率")
    @Length(max = 10)
    private String taxRate;

    /**
     * 含税金额
     */
    @ExcelProperty("含税金额")
    @Length(max = 50)
    private String amountWithTax;

    /**
     * 店铺类型（Hyper或Sams）
     */
    @ExcelProperty("店铺类型")
    @Length(max = 20)
    private String storeType;

    /**
     * 批次号  -- 导出新增字段
     */
    @ExcelProperty("批次号")
    private String jobName;

    /**
     * 数据校验异常信息
     */
    @ExcelProperty("异常数据描述")
    private String checkRemark;

}
