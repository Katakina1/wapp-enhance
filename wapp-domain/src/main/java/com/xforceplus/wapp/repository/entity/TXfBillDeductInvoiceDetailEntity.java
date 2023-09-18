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
    * 业务单发票明细关系表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2022-09-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_bill_deduct_invoice_detail")
public class TXfBillDeductInvoiceDetailEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 业务单ID
     */
    @TableField("deduct_id")
    private Long deductId;

    /**
     * 发票明细ID
     */
    @TableField("invoice_detail_id")
    private Long invoiceDetailId;

    /**
     * 结算单明细ID
     */
    @TableField("settlement_item_id")
    private Long settlementItemId;

    /**
     * 业务单据编号
     */
    @TableField("business_no")
    private String businessNo;

    /**
     * 业务单据类型 1索赔 2协议 3EPD
     */
    @TableField("business_type")
    private Integer businessType;

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

    /**
     * 占用单价
     */
    @TableField("use_unit_price")
    private BigDecimal useUnitPrice;


    public static final String ID = "id";

    public static final String DEDUCT_ID = "deduct_id";

    public static final String INVOICE_DETAIL_ID = "invoice_detail_id";

    public static final String SETTLEMENT_ITEM_ID = "settlement_item_id";

    public static final String BUSINESS_NO = "business_no";

    public static final String BUSINESS_TYPE = "business_type";

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

    public static final String USE_UNIT_PRICE = "use_unit_price";

}
