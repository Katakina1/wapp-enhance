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
    * 原始EPD单LOG明细
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_origin_epd_log_item")
public class TXfOriginEpdLogItemEntity extends BaseEntity {

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
    @TableField(value="update_time", update="getdate()" )
    private Date updateTime;

    @TableField("reference")
    private String reference;

    @TableField("tax_rate")
    private String taxRate;

    @TableField("after_payment_block")
    private String afterPaymentBlock;

    @TableField("cocd")
    private String cocd;

    @TableField("gl")
    private String gl;

    @TableField("epd_document_number")
    private String epdDocumentNumber;

    @TableField("epd_company_code")
    private String epdCompanyCode;

    @TableField("before_payment_block")
    private String beforePaymentBlock;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("year")
    private String year;

    @TableField("discount_rate")
    private String discountRate;

    @TableField("time")
    private String time;

    @TableField("vendor")
    private String vendor;

    @TableField("profit_ctr")
    private String profitCtr;

    @TableField("user_name")
    private String userName;

    @TableField("epd_fiscal_year")
    private String epdFiscalYear;

    @TableField("document_no")
    private String documentNo;

    @TableField("cl")
    private String cl;

    @TableField("discount_amount")
    private String discountAmount;

    @TableField("amount")
    private String amount;

    @TableField("date_in_format_yyyymmdd")
    private String dateInFormatYyyymmdd;

    @TableField("status_message")
    private String statusMessage;

    @TableField("doc_type")
    private String docType;

    @TableField("natural_number")
    private String naturalNumber;

    @TableField("itm")
    private String itm;

    @TableField("epd_corresponding_number")
    private String epdCorrespondingNumber;

    @TableField("department_number")
    private String departmentNumber;

    @TableField("dc")
    private String dc;

    @TableField("status")
    private String status;

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

    public static final String CREATE_USER = "create_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_USER = "update_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String REFERENCE = "reference";

    public static final String TAX_RATE = "tax_rate";

    public static final String AFTER_PAYMENT_BLOCK = "after_payment_block";

    public static final String COCD = "cocd";

    public static final String GL = "gl";

    public static final String EPD_DOCUMENT_NUMBER = "epd_document_number";

    public static final String EPD_COMPANY_CODE = "epd_company_code";

    public static final String BEFORE_PAYMENT_BLOCK = "before_payment_block";

    public static final String ID = "id";

    public static final String YEAR = "year";

    public static final String DISCOUNT_RATE = "discount_rate";

    public static final String TIME = "time";

    public static final String VENDOR = "vendor";

    public static final String PROFIT_CTR = "profit_ctr";

    public static final String USER_NAME = "user_name";

    public static final String EPD_FISCAL_YEAR = "epd_fiscal_year";

    public static final String DOCUMENT_NO = "document_no";

    public static final String CL = "cl";

    public static final String DISCOUNT_AMOUNT = "discount_amount";

    public static final String AMOUNT = "amount";

    public static final String DATE_IN_FORMAT_YYYYMMDD = "date_in_format_yyyymmdd";

    public static final String STATUS_MESSAGE = "status_message";

    public static final String DOC_TYPE = "doc_type";

    public static final String NATURAL_NUMBER = "natural_number";

    public static final String ITM = "itm";

    public static final String EPD_CORRESPONDING_NUMBER = "epd_corresponding_number";

    public static final String DEPARTMENT_NUMBER = "department_number";

    public static final String DC = "dc";

    public static final String STATUS = "status";

    public static final String CHECK_STATUS = "check_status";

    public static final String CHECK_REMARK = "check_remark";
}
