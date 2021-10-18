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
    * 业务单据明细匹配关系表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_bill_deduct_item_ref")
public class TXfBillDeductItemRefEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主信息id
     */
    @TableField("deduct_id")
    private Long deductId;

    /**
     * 明细id
     */
    @TableField("deduct_item_id")
    private Long deductItemId;

    /**
     * 使用额度
     */
    @TableField("use_amount")
    private BigDecimal useAmount;

    /**
     * 单价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 数量
     */
    @TableField("quantity")
    private BigDecimal quantity;

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

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("create_date")
    private Date createDate;

    @TableField("update_date")
    private Date updateDate;


    public static final String DEDUCT_ID = "deduct_id";

    public static final String DEDUCT_ITEM_ID = "deduct_item_id";

    public static final String USE_AMOUNT = "use_amount";

    public static final String PRICE = "price";

    public static final String QUANTITY = "quantity";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String AMOUNT_WITH_TAX = "amount_with_tax";

    public static final String ID = "id";

    public static final String CREATE_DATE = "create_date";

    public static final String UPDATE_DATE = "update_date";

}
