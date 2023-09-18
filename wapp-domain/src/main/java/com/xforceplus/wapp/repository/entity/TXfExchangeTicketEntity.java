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
    * 
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2022-06-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_exchange_ticket")
public class TXfExchangeTicketEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("id")
    private Long id;

    /**
     * 供应商6D
     */
    @TableField("vender_id")
    private String venderId;

    /**
     * 供应商名称
     */
    @TableField("vender_name")
    private String venderName;

    /**
     * JV_CODE
     */
    @TableField("jv_code")
    private String jvCode;

    /**
     * 供应商税号
     */
    @TableField("xf_tax_no")
    private String xfTaxNo;

    /**
     * 发票号码
     */
    @TableField("invoice_no")
    private String invoiceNo;

    /**
     * 开票日期
     */
    @TableField("paper_date")
    private String paperDate;

    /**
     * 发票代码
     */
    @TableField("invoice_code")
    private String invoiceCode;

    /**
     * 税额
     */
    @TableField("tax_amount")
    private BigDecimal taxAmount;

    /**
     * 税率
     */
    @TableField("tax_rate")
    private BigDecimal taxRate;

    /**
     * 不含税金额
     */
    @TableField("amount_without_tax")
    private BigDecimal amountWithoutTax;

    /**
     * 价税合计
     */
    @TableField("amount_with_tax")
    private BigDecimal amountWithTax;

    /**
     * 换票后发票号码
     */
    @TableField("exchange_invoice_no")
    private String exchangeInvoiceNo;

    /**
     * 换票后发票代码
     */
    @TableField("exchange_invoice_code")
    private String exchangeInvoiceCode;

    /**
     * 换票后税额
     */
    @TableField("exchange_tax_amount")
    private BigDecimal exchangeTaxAmount;

    /**
     * 换票后税率
     */
    @TableField("exchange_tax_rate")
    private BigDecimal exchangeTaxRate;

    /**
     * 换票开票日期
     */
    @TableField("exchange_paper_date")
    private String exchangePaperDate;

    /**
     * 换票后不含税金额
     */
    @TableField("exchange_amount_without_tax")
    private BigDecimal exchangeAmountWithoutTax;

    /**
     * 换票后价税合计
     */
    @TableField("exchange_amount_with_tax")
    private BigDecimal exchangeAmountWithTax;

    /**
     * 换票状态 0:待审核  1：待换票、2：已上传、3：已完成、4：换票失败
     */
    @TableField("exchange_status")
    private String exchangeStatus;

    /**
     * 换票来源 1:专票下发 2：手工导入
     */
    @TableField("exchange_soource")
    private String exchangeSoource;

    /**
     * 换票类型 0：纸票 1：电票
     */
    @TableField("exchange_type")
    private String exchangeType;

    /**
     * 换票原因
     */
    @TableField("exchange_reason")
    private String exchangeReason;

    /**
     * 换票商品类型 1：商品 2：费用 3：外部红票 4：内部红票 5：固定资产 6：租赁 0001：租赁 7:直接认证
     */
    @TableField("flow_type")
    private String flowType;

    /**
     * 凭证号
     */
    @TableField("voucher_no")
    private String voucherNo;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField("create_date")
    private Date createDate;

    /**
     * 修改人
     */
    @TableField("last_update_user")
    private String lastUpdateUser;

    /**
     * 修改时间
     */
    @TableField("last_update_date")
    private Date lastUpdateDate;

    @TableField("exchange_remark")
    private String exchangeRemark;

    @TableField("invoice_id")
    private String invoiceId;
    @TableField(exist = false)
    private String authStatus;
    @TableField(exist = false)
    private String exchangeAuthStatus;
    @TableField(exist = false)
    private String createDateStr;
    @TableField(exist = false)
    private String lastUpdateDateStr;

    public static final String ID = "id";

    public static final String VENDER_ID = "vender_id";

    public static final String VENDER_NAME = "vender_name";

    public static final String JV_CODE = "jv_code";

    public static final String XF_TAX_NO = "xf_tax_no";

    public static final String INVOICE_NO = "invoice_no";

    public static final String PAPER_DATE = "paper_date";

    public static final String INVOICE_CODE = "invoice_code";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String TAX_RATE = "tax_rate";

    public static final String AMOUNT_WITHOUT_TAX = "amount_without_tax";

    public static final String AMOUNT_WITH_TAX = "amount_with_tax";

    public static final String EXCHANGE_INVOICE_NO = "exchange_invoice_no";

    public static final String EXCHANGE_INVOICE_CODE = "exchange_invoice_code";

    public static final String EXCHANGE_TAX_AMOUNT = "exchange_tax_amount";

    public static final String EXCHANGE_TAX_RATE = "exchange_tax_rate";

    public static final String EXCHANGE_PAPER_DATE = "exchange_paper_date";

    public static final String EXCHANGE_AMOUNT_WITHOUT_TAX = "exchange_amount_without_tax";

    public static final String EXCHANGE_AMOUNT_WITH_TAX = "exchange_amount_with_tax";

    public static final String EXCHANGE_STATUS = "exchange_status";

    public static final String EXCHANGE_SOOURCE = "exchange_soource";

    public static final String EXCHANGE_TYPE = "exchange_type";

    public static final String EXCHANGE_REASON = "exchange_reason";

    public static final String FLOW_TYPE = "flow_type";

    public static final String CREATE_USER = "create_user";

    public static final String CREATE_DATE = "create_date";

    public static final String LAST_UPDATE_USER = "last_update_user";

    public static final String LAST_UPDATE_DATE = "last_update_date";

    public static final String EXCHANGE_REMARK = "exchange_remark";

    public static final String INVOICE_ID = "invoice_id";

}
