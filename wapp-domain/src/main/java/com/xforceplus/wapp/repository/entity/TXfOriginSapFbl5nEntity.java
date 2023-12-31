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
    * 原始协议单SAP-FBL5N
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_origin_sap_fbl5n")
public class TXfOriginSapFbl5nEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务id 关联t_xf_bill_job主键
     */
    @TableField("job_id")
    private Integer jobId;

    @TableField("clearing_date")
    private String clearingDate;

    @TableField("amount_in_doc_curr")
    private String amountInDocCurr;

    @TableField(value="update_time", update="getdate()" )
    private Date updateTime;

    @TableField("reason_code")
    private String reasonCode;

    @TableField("account")
    private String account;

    @TableField("document_date")
    private String documentDate;

    @TableField("document_number")
    private String documentNumber;

    @TableField("cleared_open_items_symbol ")
    private String clearedOpenItemsSymbol ;

    @TableField("text")
    private String text;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_user")
    private String updateUser;

    @TableField("company_code")
    private String companyCode;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("department")
    private String department;

    @TableField("tax_code")
    private String taxCode;

    @TableField("reference_key_2")
    private String referenceKey2;

    @TableField("create_user")
    private String createUser;

    @TableField("document_header_text")
    private String documentHeaderText;

    @TableField("reference")
    private String reference;

    @TableField("posting_date")
    private String postingDate;

    @TableField("document_type")
    private String documentType;

    /**
     * 数据校验状态；0:正常;1:异常
     */
    @TableField("check_status")
    private Integer checkStatus;

    /**
     * 数据校验异常信息
     */
    @TableField("check_remark")
    private String checkRemark;

    public static final String JOB_ID = "job_id";

    public static final String CLEARING_DATE = "clearing_date";

    public static final String AMOUNT_IN_DOC_CURR = "amount_in_doc_curr";

    public static final String UPDATE_TIME = "update_time";

    public static final String REASON_CODE = "reason_code";

    public static final String ACCOUNT = "account";

    public static final String DOCUMENT_DATE = "document_date";

    public static final String DOCUMENT_NUMBER = "document_number";

    public static final String CLEARED_OPEN_ITEMS_SYMBOL  = "cleared_open_items_symbol ";

    public static final String TEXT = "text";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_USER = "update_user";

    public static final String COMPANY_CODE = "company_code";

    public static final String ID = "id";

    public static final String DEPARTMENT = "department";

    public static final String TAX_CODE = "tax_code";

    public static final String REFERENCE_KEY_2 = "reference_key_2";

    public static final String CREATE_USER = "create_user";

    public static final String DOCUMENT_HEADER_TEXT = "document_header_text";

    public static final String REFERENCE = "reference";

    public static final String POSTING_DATE = "posting_date";

    public static final String DOCUMENT_TYPE = "document_type";

    public static final String CHECK_STATUS = "check_status";

    public static final String CHECK_REMARK = "check_remark";
}
