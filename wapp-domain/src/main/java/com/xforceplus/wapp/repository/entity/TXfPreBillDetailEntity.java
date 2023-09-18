package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 预制发票明细同结算单明细关系
 * @date : 2022/09/05 17:22
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_pre_bill_detail")
public class TXfPreBillDetailEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableField("id")
    private Long id;

    /**
     * 结算单id
     */
    @TableField("settlement_id")
    private Long settlementId;

    /**
     * 结算单明细id
     */
    @TableField("settlement_item_id")
    private Long settlementItemId;

    /**
     * 结算单号
     */
    @TableField("settlement_no")
    private String settlementNo;

    /**
     * 结算单明细号
     */
    @TableField("settlement_item_no")
    private String settlementItemNo;

    /**
     * 预制发票id
     */
    @TableField("pre_invoice_id")
    private Long preInvoiceId;

    /**
     * 预制发票明细id
     */
    @TableField("pre_invoice_item_id")
    private Long preInvoiceItemId;

    /**
     * 含税金额
     */
    @TableField("amount_with_tax")
    private BigDecimal amountWithTax;

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
     * 更新时间
     */
    @TableField(value="update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    public static final String ID = "id";
    public static final String SETTLEMENT_ID = "settlement_id";
    public static final String SETTLEMENT_ITEM_ID = "settlement_item_id";
    public static final String SETTLEMENT_NO = "settlement_no";
    public static final String SETTLEMENT_ITEM_NO = "settlement_item_no";
    public static final String PRE_INVOICE_ID = "pre_invoice_id";
    public static final String PRE_INVOICE_ITEM_ID = "pre_invoice_item_id";
    public static final String AMOUNT_WITH_TAX = "amount_with_tax";
    public static final String AMOUNT_WITHOUT_TAX = "amount_without_tax";
    public static final String TAX_AMOUNT = "tax_amount";
    public static final String CREATE_TIME = "create_time";
    public static final String UPDATE_TIME = "update_time";
}
