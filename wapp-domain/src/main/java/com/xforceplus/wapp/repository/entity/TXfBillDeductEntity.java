package com.xforceplus.wapp.repository.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xforceplus.wapp.repository.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
/**
 * <p>
    * 业务单据信息
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-12-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_bill_deduct")
public class TXfBillDeductEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 业务单据编号
     */
    @TableField("business_no")
    private String businessNo;

    /**
     * 业务单据类型;1:索赔;2:协议;3:EPD
     */
    @TableField("business_type")
    private Integer businessType;

    /**
     * 关联结算单编码
     */
    @TableField("ref_settlement_no")
    private String refSettlementNo;

    /**
     * 定案、入账日期
     */
    @TableField("verdict_date")
    private Date verdictDate;

    /**
     * 扣款日期
     */
    @TableField("deduct_date")
    private Date deductDate;

    /**
     * 所扣发票
     */
    @TableField("deduct_invoice")
    private String deductInvoice;

    /**
     * 税率
     */
    @TableField("tax_rate")
    private BigDecimal taxRate;

    /**
     * 协议类型编码
     */
    @TableField("agreement_reason_code")
    private String agreementReasonCode;

    /**
     * 协议类型
     */
    @TableField("agreement_reference")
    private String agreementReference;

    /**
     * 协议税码
     */
    @TableField("agreement_tax_code")
    private String agreementTaxCode;

    /**
     * sap供应商编号
     */
    @TableField("agreement_memo")
    private String agreementMemo;

    /**
     * 协议凭证号码/文档编码
     */
    @TableField("agreement_document_number")
    private String agreementDocumentNumber;

    /**
     * 协议凭证类型/文档类型
     */
    @TableField("agreement_document_type")
    private String agreementDocumentType;

    /**
     * 税额
     */
    @TableField("tax_amount")
    private BigDecimal taxAmount;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 业务单状态
索赔单:101待匹配明细;102待确认税编;103待确认税差;104待匹配蓝票;105:待匹配结算单;106已匹配结算单;107待审核;108已撤销
协议单:201待匹配结算单;202已匹配结算单;204待匹配蓝票;206已作废
EPD单:301待匹配结算单;302已匹配结算单;303待匹配蓝票;304已作废
1已锁定
0解锁
     */
    @TableField("status")
    private Integer status;

    /**
     * 扣款公司jv_code
     */
    @TableField("purchaser_no")
    private String purchaserNo;

    /**
     * 供应商编码
     */
    @TableField("seller_no")
    private String sellerNo;

    /**
     * 供应商名称
     */
    @TableField("seller_name")
    private String sellerName;

    /**
     * 不含税金额
     */
    @TableField("amount_without_tax")
    private BigDecimal amountWithoutTax;

    /**
     * 含税金额
     */
    @TableField("amount_with_tax")
    private BigDecimal amountWithTax;

    /**
     * 锁定状态 1 锁定 0 未锁定
     */
    @TableField("lock_flag")
    private Integer lockFlag;

    /**
     * 批次号
     */
    @TableField("batch_no")
    private String batchNo;

    /**
     * 来源id，唯一标识
     */
    @TableField("source_id")
    private Long sourceId;

    /**
     * 购方名称、扣款公司名称
     */
    @TableField("purchaser_name")
    private String purchaserName;

    @TableField(value="update_time", update="getdate()" )
    private Date updateTime;

    @TableField("create_time")
    private Date createTime;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;


    /**
     * 例外报告code
     */
    @TableField("exception_code")
    private String exceptionCode;

    /**
     * 例外报告说明
     */
    @TableField("exception_description")
    private String exceptionDescription;

    /**
     * 列外报告处理状态
     */
    @TableField("exception_status")
    private Integer exceptionStatus;

    /**
     * 业务单开票状态(0:未开票;1:部分开票;2:已开票)
     */
    @TableField("make_invoice_status")
    private Integer makeInvoiceStatus;

    public static final String BUSINESS_NO = "business_no";

    public static final String BUSINESS_TYPE = "business_type";

    public static final String REF_SETTLEMENT_NO = "ref_settlement_no";

    public static final String VERDICT_DATE = "verdict_date";

    public static final String DEDUCT_DATE = "deduct_date";

    public static final String DEDUCT_INVOICE = "deduct_invoice";

    public static final String TAX_RATE = "tax_rate";

    public static final String AGREEMENT_REASON_CODE = "agreement_reason_code";

    public static final String AGREEMENT_REFERENCE = "agreement_reference";

    public static final String AGREEMENT_TAX_CODE = "agreement_tax_code";

    public static final String AGREEMENT_MEMO = "agreement_memo";

    public static final String AGREEMENT_DOCUMENT_NUMBER = "agreement_document_number";

    public static final String AGREEMENT_DOCUMENT_TYPE = "agreement_document_type";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String REMARK = "remark";

    public static final String STATUS = "status";

    public static final String PURCHASER_NO = "purchaser_no";

    public static final String SELLER_NO = "seller_no";

    public static final String SELLER_NAME = "seller_name";

    public static final String AMOUNT_WITHOUT_TAX = "amount_without_tax";

    public static final String AMOUNT_WITH_TAX = "amount_with_tax";

    public static final String LOCK_FLAG = "lock_flag";

    public static final String BATCH_NO = "batch_no";

    public static final String SOURCE_ID = "source_id";

    public static final String PURCHASER_NAME = "purchaser_name";

    public static final String UPDATE_TIME = "update_time";

    public static final String CREATE_TIME = "create_time";

    public static final String ID = "id";

    public static final String EXCEPTION_CODE = "exception_code";

    public static final String EXCEPTION_DESCRIPTION = "exception_description";

    public static final String EXCEPTION_STATUS = "exception_status";

    public static final String MAKE_INVOICE_STATUS = "make_invoice_status";

}
