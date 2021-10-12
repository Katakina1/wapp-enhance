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
    * 业务单据匹配蓝票
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_bill_deduct_invoice")
public class TXfBillDeductInvoiceEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 业务单据编号
     */
    @TableField("business_no")
    private String businessNo;

    /**
     * 业务单据类型;1:索赔;2:结算单
     */
    @TableField("business_type")
    private Integer businessType;

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
     * 使用额度
     */
    @TableField("use_amount")
    private Double useAmount;

    /**
     * 状态;0正常;1撤销
     */
    @TableField("status")
    private Integer status;

    @TableField("update_date")
    private Date updateDate;

    @TableField("create_date")
    private Date createDate;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    public static final String BUSINESS_NO = "business_no";

    public static final String BUSINESS_TYPE = "business_type";

    public static final String INVOICE_CODE = "invoice_code";

    public static final String INVOICE_NO = "invoice_no";

    public static final String USE_AMOUNT = "use_amount";

    public static final String STATUS = "status";

    public static final String UPDATE_DATE = "update_date";

    public static final String CREATE_DATE = "create_date";

    public static final String ID = "id";

}
