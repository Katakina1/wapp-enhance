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
    * 原始协议单数据
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_origin_agreement_bill")
public class TXfOriginAgreementBillEntity extends BaseEntity {

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

    /**
     * 客户编码
     */
    @TableField("customer_number")
    private String customerNumber;

    /**
     * 客户名称
     */
    @TableField("customer_name")
    private String customerName;

    /**
     * 金额(含税)
     */
    @TableField("amount_with_tax")
    private String amountWithTax;

    /**
     * 协议类型编码
     */
    @TableField("reason_code")
    private String reasonCode;

    /**
     * 协议号
     */
    @TableField("reference")
    private String reference;

    /**
     * 税码
     */
    @TableField("tax_code")
    private String taxCode;

    /**
     * 扣款日期
     */
    @TableField("clearing_date")
    private String clearingDate;

    /**
     * 税率
     */
    @TableField("tax_rate")
    private String taxRate;

    /**
     * 供应商6D
     */
    @TableField("memo")
    private String memo;

    /**
     * 协议类型
     */
    @TableField("reference_type")
    private String referenceType;

    /**
     * 扣款公司编码
     */
    @TableField("company_code")
    private String companyCode;

    /**
     * 凭证编号
     */
    @TableField("document_number")
    private String documentNumber;

    /**
     * 凭证类型
     */
    @TableField("document_type")
    private String documentType;

    /**
     * 入账日期
     */
    @TableField("posting_date")
    private String postingDate;

    /**
     * 税额
     */
    @TableField("tax_amount")
    private String taxAmount;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    public static final String JOB_ID = "job_id";

    public static final String CREATE_USER = "create_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_USER = "update_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String CUSTOMER_NUMBER = "customer_number";

    public static final String CUSTOMER_NAME = "customer_name";

    public static final String AMOUNT_WITH_TAX = "amount_with_tax";

    public static final String REASON_CODE = "reason_code";

    public static final String REFERENCE = "reference";

    public static final String TAX_CODE = "tax_code";

    public static final String CLEARING_DATE = "clearing_date";

    public static final String TAX_RATE = "tax_rate";

    public static final String MEMO = "memo";

    public static final String REFERENCE_TYPE = "reference_type";

    public static final String COMPANY_CODE = "company_code";

    public static final String DOCUMENT_NUMBER = "document_number";

    public static final String DOCUMENT_TYPE = "document_type";

    public static final String POSTING_DATE = "posting_date";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String ID = "id";

}
