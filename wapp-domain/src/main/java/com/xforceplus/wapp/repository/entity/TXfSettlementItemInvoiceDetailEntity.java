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
    * 结算单明细发票明细关系表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2022-09-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_settlement_item_invoice_detail")
public class TXfSettlementItemInvoiceDetailEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 结算单明细ID
     */
    @TableField("settlement_item_id")
    private Long settlementItemId;

    /**
     * 发票明细ID
     */
    @TableField("invoice_detail_id")
    private Long invoiceDetailId;

    /**
     * 结算单ID
     */
    @TableField("settlement_id")
    private Long settlementId;

    /**
     * 结算单编号
     */
    @TableField("settlement_no")
    private String settlementNo;

    /**
     * 结算单类型 1索赔 2协议 3EPD
     */
    @TableField("settlement_type")
    private Integer settlementType;

    /**
     * 发票ID
     */
    @TableField("invoice_id")
    private Long invoiceId;

    /**
     * 发票代码
     */
    @TableField("invoice_code")
    private String invoiceCode;

    /**
     * 发票编码
     */
    @TableField("invoice_no")
    private String invoiceNo;

    /**
     * 正负混合标记 0-无 1-有
     */
    @TableField("plus_minus_flag")
    private Integer plusMinusFlag;

    /**
     * 税率
     */
    @TableField("tax_rate")
    private BigDecimal taxRate;

    /**
     * 占用不含税
     */
    @TableField("use_amount_without_tax")
    private BigDecimal useAmountWithoutTax;

    /**
     * 占用含税
     */
    @TableField("use_amount_with_tax")
    private BigDecimal useAmountWithTax;

    /**
     * 占用税额
     */
    @TableField("use_tax_amount")
    private BigDecimal useTaxAmount;

    /**
     * 占用个数
     */
    @TableField("use_quantity")
    private BigDecimal useQuantity;

    /**
     * 是否成品油 0-否 1-是
     */
    @TableField("is_oil")
    private Integer isOil;

    /**
     * 状态 0正常 1撤销
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value="update_time", update="getdate()" )
    private Date updateTime;


    public static final String ID = "id";

    public static final String SETTLEMENT_ITEM_ID = "settlement_item_id";

    public static final String INVOICE_DETAIL_ID = "invoice_detail_id";

    public static final String SETTLEMENT_ID = "settlement_id";

    public static final String SETTLEMENT_NO = "settlement_no";

    public static final String SETTLEMENT_TYPE = "settlement_type";

    public static final String INVOICE_ID = "invoice_id";

    public static final String INVOICE_CODE = "invoice_code";

    public static final String INVOICE_NO = "invoice_no";

    public static final String PLUS_MINUS_FLAG = "plus_minus_flag";

    public static final String TAX_RATE = "tax_rate";

    public static final String USE_AMOUNT_WITHOUT_TAX = "use_amount_without_tax";

    public static final String USE_AMOUNT_WITH_TAX = "use_amount_with_tax";

    public static final String USE_TAX_AMOUNT = "use_tax_amount";

    public static final String USE_QUANTITY = "use_quantity";

    public static final String IS_OIL = "is_oil";

    public static final String STATUS = "status";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

}
