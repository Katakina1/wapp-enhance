package com.xforceplus.wapp.repository.entity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
    * 业务单据信息
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-28
 */
@Data
public class TXfBillDeductExtEntity {

    private static final long serialVersionUID = 1L;

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
索赔单:101待匹配明细;102待确认税编;103待确认税差;104待匹配蓝票;105:待匹配结算单;106已匹配结算单;107待审核;108已撤销
协议单:201待匹配结算单;202已匹配结算单;204待匹配蓝票;206已作废
EPD单:301待匹配结算单;302已匹配结算单;303待匹配蓝票;304已作废
1已锁定
0解锁
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

    private Date createTime;

    private Long id;

    private Date updateTime;
    /**
     * 明细总不含税金额
     */
    private BigDecimal itemWithoutAmount;
    /**
     * 明细总含税金额
     */
    private BigDecimal itemWithAmount;
    /**
     * 明细总税额
     */
    private BigDecimal itemTaxAmount;


    private String invoiceType;

    /** 结算单状态 */
    private Integer settlementStatus;

    /** 结算单备注 */
    private String settlementRemark;

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

    /**
     * 业务单开票状态(0:未开票;1:部分开票;2:已开票)
     */
    private Integer makeInvoiceStatus;


}
