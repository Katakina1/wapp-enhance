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
    * 协议数据处理临时表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_origin_agreement_merge")
public class TXfOriginAgreementMergeEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务id
     */
    @TableField("job_id")
    private Integer jobId;

    /**
     * 客户编码
     */
    @TableField("customer_no")
    private String customerNo;

    /**
     * 客户名称
     */
    @TableField("customer_name")
    private String customerName;

    /**
     * 扣款公司编码
     */
    @TableField("company_code")
    private String companyCode;

    /**
     * 含税金额
     */
    @TableField("with_amount")
    private BigDecimal withAmount;

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
     * 税率
     */
    @TableField("tax_rate")
    private BigDecimal taxRate;

    /**
     * 扣款日期
     */
    @TableField("deduct_date")
    private Date deductDate;

    /**
     * 供应商6D
     */
    @TableField("memo")
    private String memo;

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
    @TableField("post_date")
    private Date postDate;

    /**
     * 税额
     */
    @TableField("tax_amount")
    private BigDecimal taxAmount;

    /**
     * 来源;1:FBL5N;2:ZARR0355
     */
    @TableField("source")
    private Integer source;

    @TableField(value="update_time", update="getdate()" )
    private Date updateTime;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("create_time")
    private Date createTime;


    public static final String JOB_ID = "job_id";

    public static final String CUSTOMER_NO = "customer_no";

    public static final String CUSTOMER_NAME = "customer_name";

    public static final String COMPANY_CODE = "company_code";

    public static final String WITH_AMOUNT = "with_amount";

    public static final String REASON_CODE = "reason_code";

    public static final String REFERENCE = "reference";

    public static final String TAX_CODE = "tax_code";

    public static final String TAX_RATE = "tax_rate";

    public static final String DEDUCT_DATE = "deduct_date";

    public static final String MEMO = "memo";

    public static final String DOCUMENT_NUMBER = "document_number";

    public static final String DOCUMENT_TYPE = "document_type";

    public static final String POST_DATE = "post_date";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String SOURCE = "source";

    public static final String UPDATE_TIME = "update_time";

    public static final String ID = "id";

    public static final String CREATE_TIME = "create_time";

}
