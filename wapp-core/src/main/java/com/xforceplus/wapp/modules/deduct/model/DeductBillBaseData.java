package com.xforceplus.wapp.modules.deduct.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 类描述：协议单，EPD,索赔的通用数据
 *
 * @ClassName DeductBillBaseData
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/12 10:31
 */
@Data
public class DeductBillBaseData {
    /**
     * 业务单据编号
     */
    private String businessNo;
    /**
     * 业务单据类型;1:索赔;2:协议;3:EPD
     */
    private Integer businessType;
    /**
     * 供应商编号
     */
    private String sellerNo;
    /**
     * 供应商名称
     */
    private String sellerName;
    /**
     * 扣款日期
     */
    private Date deductDate;
    /**
     * 扣款公司jv_code
     */
    private String purchaserNo;
    /**
     * 不含税金额
     */
    private BigDecimal amountWithoutTax;
    /**
     * 含税金额
     */
    private BigDecimal amountWithTax;
    /**
     * 税额
     */
    private BigDecimal taxAmount;
    /**
     * 备注
     */
    private String remark;
    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 批次号
     */
    private String batchNo;
}
