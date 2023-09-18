package com.xforceplus.wapp.repository.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Describe: 供应商-业务单据信息
 *
 * @Author xiezhongyong
 * @Date 2022/9/16
 */
@Data
public class TXfSellerBillDeductExtEntity {

    /**
     * 业务单ID
     */
    private Long id;

    /**
     * 业务单据编号
     */
    private String businessNo;

    /**
     * 业务单据类型;1:索赔;2:协议;3:EPD
     */
    private Integer businessType;

    /**
     * 关联结算单编码
     */
    private String refSettlementNo;

    /**
     * 定案、入账日期
     */
    private Date verdictDate;

    /**
     * 扣款日期
     */
    private Date deductDate;

    /**
     * 所扣发票
     */
    private String deductInvoice;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 协议类型编码
     */
    private String agreementReasonCode;

    /**
     * 协议号
     */
    private String agreementReference;

    /**
     * 协议税码
     */
    private String agreementTaxCode;

    /**
     * 协议供应商6D
     */
    private String agreementMemo;

    /**
     * 协议凭证号码
     */
    private String agreementDocumentNumber;

    /**
     * 协议凭证类型
     */
    private String agreementDocumentType;

    /**
     * 税额
     */
    private BigDecimal taxAmount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 业务单状态
     */
    private Integer status;

    /**
     * 扣款公司jv_code
     */
    private String purchaserNo;

    /**
     * 供应商编码
     */
    private String sellerNo;

    /**
     * 供应商名称
     */
    private String sellerName;

    /**
     * 不含税金额
     */
    private BigDecimal amountWithoutTax;

    /**
     * 含税金额
     */
    private BigDecimal amountWithTax;

    /**
     * 锁定状态 1 锁定 0 未锁定
     */
    private Integer lockFlag;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 来源id，唯一标识
     */
    private Long sourceId;

    /**
     * 购方名称、扣款公司名称
     */
    private String purchaserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 结算单状态
     */
    private Integer settlementStatus;

    /**
     * 例外报告code
     */
    private String exceptionCode;

    /**
     * 例外报告说明
     */
    private String exceptionDescription;

    /**
     * 列外报告处理状态
     */
    private Integer exceptionStatus;

}
