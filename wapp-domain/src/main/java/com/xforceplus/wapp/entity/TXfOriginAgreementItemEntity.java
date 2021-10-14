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
    * 原始协议单明细1 SAP-ZARR0355原稿
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_origin_agreement_item")
public class TXfOriginAgreementItemEntity extends BaseEntity {

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

    @TableField("amount_with_tax")
    private String amountWithTax;

    @TableField("quant")
    private String quant;

    @TableField("customer_number")
    private String customerNumber;

    @TableField("contents")
    private String contents;

    @TableField("internal_invoice_no")
    private String internalInvoiceNo;

    @TableField("profit_centre")
    private String profitCentre;

    @TableField("unit_prices")
    private String unitPrices;

    @TableField("measurements")
    private String measurements;

    @TableField("memo")
    private String memo;

    @TableField("reason_code")
    private String reasonCode;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("sap_accounting_document")
    private String sapAccountingDocument;

    @TableField("customer")
    private String customer;

    @TableField("sequence_number")
    private String sequenceNumber;


    public static final String JOB_ID = "job_id";

    public static final String CREATE_USER = "create_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_USER = "update_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String AMOUNT_WITH_TAX = "amount_with_tax";

    public static final String QUANT = "quant";

    public static final String CUSTOMER_NUMBER = "customer_number";

    public static final String CONTENTS = "contents";

    public static final String INTERNAL_INVOICE_NO = "internal_invoice_no";

    public static final String PROFIT_CENTRE = "profit_centre";

    public static final String UNIT_PRICES = "unit_prices";

    public static final String MEASUREMENTS = "measurements";

    public static final String MEMO = "memo";

    public static final String REASON_CODE = "reason_code";

    public static final String ID = "id";

    public static final String SAP_ACCOUNTING_DOCUMENT = "sap_accounting_document";

    public static final String CUSTOMER = "customer";

    public static final String SEQUENCE_NUMBER = "sequence_number";

}
