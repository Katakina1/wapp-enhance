package com.xforceplus.wapp.repository.entity;

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
    * 换票表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-12-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_invoice_exchange")
public class TXfInvoiceExchangeEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 底账发票id
     */
    @TableField("invoice_id")
    private Long invoiceId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 新开发票id
     */
    @TableField("new_invoice_id")
    private Long newInvoiceId;

    /**
     * 凭证号
     */
    @TableField("voucher_no")
    private String voucherNo;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;


    public static final String ID = "id";

    public static final String INVOICE_ID = "invoice_id";

    public static final String CREATE_TIME = "create_time";

    public static final String NEW_INVOICE_ID = "new_invoice_id";

    public static final String VOUCHER_NO = "voucher_no";

    public static final String REMARK = "remark";

}
