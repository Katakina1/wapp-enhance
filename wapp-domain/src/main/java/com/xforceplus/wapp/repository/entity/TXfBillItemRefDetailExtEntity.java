package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Describe: 业务单明细关联信息（结算单明细ID,预制发票明细ID,预制发票ID）
 *
 * @Author xiezhongyong
 * @Date 2022/9/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_bill_deduct")
public class TXfBillItemRefDetailExtEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 预制发票ID
     */
    @TableField("pre_invoice_id")
    private Long preInvoiceId;

    /**
     * 业务单明细ID
     */
    @TableField("deduct_item_id")
    private Long deductItemId;

    /**
     * 结算单明细ID
     */
    @TableField("settlement_item_id")
    private Long settlementItemId;

    /**
     * 预制发票明细ID
     */
    @TableField("pre_invoice_item_id")
    private Long preInvoiceItemId;


}
