package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
/**
 * <p>
    * 
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-11-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_pre_invoice")
public class TXfPreInvoiceEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 结算单编码
     */
    @TableField("settlement_no")
    private String settlementNo;

    /**
     * 购方编码
     */
    @TableField("purchaser_no")
    private String purchaserNo;

    /**
     * 购方名称
     */
    @TableField("purchaser_name")
    private String purchaserName;

    /**
     * 购方税号
     */
    @TableField("purchaser_tax_no")
    private String purchaserTaxNo;

    /**
     * 购方电话
     */
    @TableField("purchaser_tel")
    private String purchaserTel;

    /**
     * 购方地址
     */
    @TableField("purchaser_address")
    private String purchaserAddress;

    /**
     * 购方开户行
     */
    @TableField("purchaser_bank_name")
    private String purchaserBankName;

    /**
     * 购方银行账号
     */
    @TableField("purchaser_bank_account")
    private String purchaserBankAccount;

    /**
     * 供应商编码
     */
    @TableField("seller_no")
    private String sellerNo;

    /**
     * 供应商税编
     */
    @TableField("seller_tax_no")
    private String sellerTaxNo;

    /**
     * 销方名称
     */
    @TableField("seller_name")
    private String sellerName;

    /**
     * 销方电话
     */
    @TableField("seller_tel")
    private String sellerTel;

    /**
     * 供应商地址
     */
    @TableField("seller_address")
    private String sellerAddress;

    /**
     * 供应商开户行
     */
    @TableField("seller_bank_name")
    private String sellerBankName;

    /**
     * 供应商银行账号
     */
    @TableField("seller_bank_account")
    private String sellerBankAccount;

    /**
     * 发票类型
     */
    @TableField("invoice_type")
    private String invoiceType;

    /**
     * 结算单类型:1索赔单,2:协议单；3:EPD单
     */
    @TableField("settlement_type")
    private Integer settlementType;

    /**
     * 发票号码 回填时补充
     */
    @TableField("invoice_no")
    private String invoiceNo;

    /**
     * 发票代码 回填时补充
     */
    @TableField("invoice_code")
    private String invoiceCode;

    /**
     * 开票日期，回填时补充
     */
    @TableField("paper_drew_date")
    private String paperDrewDate;

    /**
     * 机器码 回填时补充
     */
    @TableField("machine_code")
    private String machineCode;

    /**
     * 校验码  回填时补充
     */
    @TableField("check_code")
    private String checkCode;

    /**
     * 不含税金额
     */
    @TableField("amount_without_tax")
    private BigDecimal amountWithoutTax;

    /**
     * 税额
     */
    @TableField("tax_amount")
    private BigDecimal taxAmount;

    /**
     * 含税金额
     */
    @TableField("amount_with_tax")
    private BigDecimal amountWithTax;

    /**
     * 税率 目前整数存储，需要程序单独处理
1---1%
9---9%

     */
    @TableField("tax_rate")
    private BigDecimal taxRate;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 预制发票状态;1:待申请红字信息表;2:待上传;3:已上传;4:待审核;5:已作废;6:正在申请红字信息中;7:已重新拆票-视同逻辑删除
     */
    @TableField("pre_invoice_status")
    private Integer preInvoiceStatus;

    /**
     * 开具结果
     */
    @TableField("process_remark")
    private String processRemark;

    /**
     * 开票规则id
     */
    @TableField("rule_id")
    private Long ruleId;

    /**
     * 红票的原始号码 回填时补充
     */
    @TableField("origin_invoice_no")
    private String originInvoiceNo;

    /**
     * 红票的原始号码 回填时补充
     */
    @TableField("origin_invoice_code")
    private String originInvoiceCode;

    /**
     * 原始发票类型 专纸：01
机动车：03
普纸：04
普电：10
卷票：11
通行费电票：14
专电：08
     */
    @TableField("origin_invoice_type")
    private String originInvoiceType;

    /**
     * 原始发票开票日期
     */
    @TableField("origin_paper_drew_date")
    private String originPaperDrewDate;

    /**
     * 红字信息编码，红票使用，回填补充
     */
    @TableField("red_notification_no")
    private String redNotificationNo;

    /**
     * 结算单id
     */
    @TableField("settlement_id")
    private Long settlementId;

    @TableField(value="update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("update_user_id")
    private Long updateUserId;

    @TableField("create_user_id")
    private Long createUserId;

    @TableField("goods_list_flag")
    private Integer goodsListFlag;

    /**
     * 是否成品油
     */
    @TableField("is_oil")
    private Integer isOil;

    public static final String SETTLEMENT_NO = "settlement_no";

    public static final String PURCHASER_NO = "purchaser_no";

    public static final String PURCHASER_NAME = "purchaser_name";

    public static final String PURCHASER_TAX_NO = "purchaser_tax_no";

    public static final String PURCHASER_TEL = "purchaser_tel";

    public static final String PURCHASER_ADDRESS = "purchaser_address";

    public static final String PURCHASER_BANK_NAME = "purchaser_bank_name";

    public static final String PURCHASER_BANK_ACCOUNT = "purchaser_bank_account";

    public static final String SELLER_NO = "seller_no";

    public static final String SELLER_TAX_NO = "seller_tax_no";

    public static final String SELLER_NAME = "seller_name";

    public static final String SELLER_TEL = "seller_tel";

    public static final String SELLER_ADDRESS = "seller_address";

    public static final String SELLER_BANK_NAME = "seller_bank_name";

    public static final String SELLER_BANK_ACCOUNT = "seller_bank_account";

    public static final String INVOICE_TYPE = "invoice_type";

    public static final String SETTLEMENT_TYPE = "settlement_type";

    public static final String INVOICE_NO = "invoice_no";

    public static final String INVOICE_CODE = "invoice_code";

    public static final String PAPER_DREW_DATE = "paper_drew_date";

    public static final String MACHINE_CODE = "machine_code";

    public static final String CHECK_CODE = "check_code";

    public static final String AMOUNT_WITHOUT_TAX = "amount_without_tax";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String AMOUNT_WITH_TAX = "amount_with_tax";

    public static final String TAX_RATE = "tax_rate";

    public static final String REMARK = "remark";

    public static final String PRE_INVOICE_STATUS = "pre_invoice_status";

    public static final String PROCESS_REMARK = "process_remark";

    public static final String RULE_ID = "rule_id";

    public static final String ORIGIN_INVOICE_NO = "origin_invoice_no";

    public static final String ORIGIN_INVOICE_CODE = "origin_invoice_code";

    public static final String ORIGIN_INVOICE_TYPE = "origin_invoice_type";

    public static final String ORIGIN_PAPER_DREW_DATE = "origin_paper_drew_date";

    public static final String RED_NOTIFICATION_NO = "red_notification_no";

    public static final String SETTLEMENT_ID = "settlement_id";

    public static final String UPDATE_TIME = "update_time";

    public static final String CREATE_TIME = "create_time";

    public static final String ID = "id";

    public static final String UPDATE_USER_ID = "update_user_id";

    public static final String CREATE_USER_ID = "create_user_id";

    public static final String GOODS_LIST_FLAG = "goods_list_flag";

    public static final String IS_OIL = "is_oil";
}
