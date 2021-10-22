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
    * 采购问题清单
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_dx_question_paper")
public class TDxQuestionPaperEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 分区
     */
    @TableField("partition")
    private String partition;

    /**
     * 采购人姓名
     */
    @TableField("purchaser")
    private String purchaser;

    /**
     * 所属JV号
     */
    @TableField("jvcode")
    private String jvcode;

    /**
     * 所属城市
     */
    @TableField("city")
    private String city;

    /**
     * 供应商号
     */
    @TableField("usercode")
    private String usercode;

    /**
     * 供应商名称
     */
    @TableField("username")
    private String username;

    /**
     * 供联系方式
     */
    @TableField("telephone")
    private String telephone;

    /**
     * 部门
     */
    @TableField("department")
    private String department;

    /**
     * 发票号
     */
    @TableField("invoiceNo")
    private String invoiceNo;

    /**
     * 开票日期
     */
    @TableField("invoiceDate")
    private Date invoiceDate;

    /**
     * 问题类型
     */
    @TableField("questionType")
    private String questionType;

    /**
     * 总金额
     */
    @TableField("totalAmount")
    private BigDecimal totalAmount;

    /**
     * 问题原因
     */
    @TableField("problemCause")
    private String problemCause;

    /**
     * 问题描述
     */
    @TableField("description")
    private String description;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 创建时间
     */
    @TableField("created_date")
    private Date createdDate;

    /**
     * 审核状态 0未审核 1审核通过 2审核不通过 3 推送bpms
     */
    @TableField("checkstatus")
    private String checkstatus;

    /**
     * 审核不通过原因
     */
    @TableField("unPassReason")
    private String unPassReason;

    /**
     * 审核日期
     */
    @TableField("check_date")
    private Date checkDate;

    /**
     * 采购批复时间
     */
    @TableField("reply_date")
    private Date replyDate;

    @TableField("problem_stream")
    private String problemStream;

    @TableField("reject_date")
    private Date rejectDate;

    @TableField("storeNbr")
    private String storeNbr;

    @TableField("bpms_id")
    private String bpmsId;


    public static final String PARTITION = "partition";

    public static final String PURCHASER = "purchaser";

    public static final String JVCODE = "jvcode";

    public static final String CITY = "city";

    public static final String USERCODE = "usercode";

    public static final String USERNAME = "username";

    public static final String TELEPHONE = "telephone";

    public static final String DEPARTMENT = "department";

    public static final String INVOICENO = "invoiceNo";

    public static final String INVOICEDATE = "invoiceDate";

    public static final String QUESTIONTYPE = "questionType";

    public static final String TOTALAMOUNT = "totalAmount";

    public static final String PROBLEMCAUSE = "problemCause";

    public static final String DESCRIPTION = "description";

    public static final String ID = "id";

    public static final String CREATED_DATE = "created_date";

    public static final String CHECKSTATUS = "checkstatus";

    public static final String UNPASSREASON = "unPassReason";

    public static final String CHECK_DATE = "check_date";

    public static final String REPLY_DATE = "reply_date";

    public static final String PROBLEM_STREAM = "problem_stream";

    public static final String REJECT_DATE = "reject_date";

    public static final String STORENBR = "storeNbr";

    public static final String BPMS_ID = "bpms_id";

}
