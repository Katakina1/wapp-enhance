package com.xforceplus.wapp.entity;

import com.xforceplus.wapp.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
/**
 * <p>
    * 原始EPD单数据
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_origin_epd_bill")
public class TXfOriginEpdBillEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务id 关联t_xf_bill_job主键
     */
    @TableField("job_id")
    private Integer jobId;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新人
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 更新时间
     */
    @TableField(value="update_time", update="now(3)" )
    private Date updateTime;

    @TableField("tax_code")
    private String taxCode;

    @TableField("document_type")
    private String documentType;

    @TableField("reference_key_1")
    private String referenceKey1;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("cash_disc_amt_lc")
    private String cashDiscAmtLc;

    @TableField("account")
    private String account;

    @TableField("clearing_date")
    private String clearingDate;

    @TableField("amount_in_local")
    private String amountInLocal;

    @TableField("reference_key_2")
    private String referenceKey2;

    @TableField("reverse_clearing")
    private String reverseClearing;

    @TableField("reference")
    private String reference;

    @TableField("payment_block")
    private String paymentBlock;

    @TableField("currency")
    private String currency;

    @TableField("posting_date")
    private String postingDate;

    @TableField("invoice_reference")
    private String invoiceReference;

    @TableField("payment_date")
    private String paymentDate;

    @TableField("company_code")
    private String companyCode;

    @TableField("text")
    private String text;


    public static final String JOB_ID = "job_id";

    public static final String CREATE_USER = "create_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_USER = "update_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String TAX_CODE = "tax_code";

    public static final String DOCUMENT_TYPE = "document_type";

    public static final String REFERENCE_KEY_1 = "reference_key_1";

    public static final String ID = "id";

    public static final String CASH_DISC_AMT_LC = "cash_disc_amt_lc";

    public static final String ACCOUNT = "account";

    public static final String CLEARING_DATE = "clearing_date";

    public static final String AMOUNT_IN_LOCAL = "amount_in_local";

    public static final String REFERENCE_KEY_2 = "reference_key_2";

    public static final String REVERSE_CLEARING = "reverse_clearing";

    public static final String REFERENCE = "reference";

    public static final String PAYMENT_BLOCK = "payment_block";

    public static final String CURRENCY = "currency";

    public static final String POSTING_DATE = "posting_date";

    public static final String INVOICE_REFERENCE = "invoice_reference";

    public static final String PAYMENT_DATE = "payment_date";

    public static final String COMPANY_CODE = "company_code";

    public static final String TEXT = "text";

}
