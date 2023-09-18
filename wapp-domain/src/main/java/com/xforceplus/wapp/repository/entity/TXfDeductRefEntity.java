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
    * 业务单信息关联表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2022-09-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_deduct_ref")
public class TXfDeductRefEntity extends BaseEntity {

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
     * 红字信息表ID
     */
    @TableField("red_notification_id")
    private Long redNotificationId;

    /**
     * 待申请   0
已申请   1
申请中   2
申请失败  3
撤销中   4
撤销失败  5
已撤销   6
     */
    @TableField("apply_status")
    private Integer applyStatus;

    /**
     * 红字信息表占用单据的金额
     */
    @TableField("amount_without_tax")
    private BigDecimal amountWithoutTax;

    /**
     * 0未删除，1删除
     */
    @TableField("deleted")
    private Integer deleted;

    /**
     * 红字信息占用业务单据的含税金额
     */
    @TableField("amount_with_tax")
    private BigDecimal amountWithTax;

    /**
     * 红字信息占用业务单的税额（以红字信息税率为准）
     */
    @TableField("tax_amount")
    private BigDecimal taxAmount;

    /**
     * 红字信息与业务单的税差（业务单税额-红字信息税额）
     */
    @TableField("tax_diff")
    private BigDecimal taxDiff;

    /**
     * 红字信息表编号
     */
    @TableField("red_notification_no")
    private String redNotificationNo;

    /**
     * 业务单号
     */
    @TableField("business_no")
    private String businessNo;

    /**
     * 预制发票ID
     */
    @TableField("pre_invoice_id")
    private Long preInvoiceId;

    @TableField("create_time")
    private Date createTime;

    @TableField(value="update_time", update="getdate()" )
    private Date updateTime;


    public static final String ID = "id";

    public static final String DEDUCT_ID = "deduct_id";

    public static final String RED_NOTIFICATION_ID = "red_notification_id";

    public static final String APPLY_STATUS = "apply_status";

    public static final String AMOUNT_WITHOUT_TAX = "amount_without_tax";

    public static final String DELETED = "deleted";

    public static final String AMOUNT_WITH_TAX = "amount_with_tax";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String TAX_DIFF = "tax_diff";

    public static final String RED_NOTIFICATION_NO = "red_notification_no";

    public static final String BUSINESS_NO = "business_no";

    public static final String PRE_INVOICE_ID = "pre_invoice_id";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

}
