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
    * 专用电票上传记录明细表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-12-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_elec_upload_record_detail")
public class TXfElecUploadRecordDetailEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，雪花算法
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 上传ofd/pdf批次号
     */
    @TableField("batch_no")
    private String batchNo;

    /**
     * pdf识别任务号
     */
    @TableField("xf_discern_task_id")
    private String xfDiscernTaskId;

    /**
     * 验真任务号
     */
    @TableField("xf_verify_task_id")
    private String xfVerifyTaskId;

    /**
     * 发票号
     */
    @TableField("invoice_no")
    private String invoiceNo;

    /**
     * 失败原因
     */
    @TableField("reason")
    private String reason;

    /**
     * 0 失败,1成功, 2待识别, 3识别验真中
     */
    @TableField("status")
    private Integer status;

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

    /**
     * 文件类型: 0-ofd文件，1-pdf文件
     */
    @TableField("file_type")
    private Integer fileType;

    /**
     * 文件服务返回-文件ID
     */
    @TableField("upload_id")
    private String uploadId;

    /**
     * 文件服务返回-文件路径
     */
    @TableField("upload_path")
    private String uploadPath;

    /**
     * 结算单号
     */
    @TableField("settlement_no")
    private String settlementNo;

    /**
     * 开票日期
     */
    @TableField("paper_drew_date")
    private String paperDrewDate;

    /**
     * 不含税金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 校验码后六位
     */
    @TableField("check_code")
    private String checkCode;

    /**
     * 发票代码
     */
    @TableField("invoice_code")
    private String invoiceCode;

    /**
     * 文件名称
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 业务类型  0结算单 1换票 2订单匹配
     */
    @TableField("business_type")
    private Integer businessType;


    public static final String ID = "id";

    public static final String BATCH_NO = "batch_no";

    public static final String XF_DISCERN_TASK_ID = "xf_discern_task_id";

    public static final String XF_VERIFY_TASK_ID = "xf_verify_task_id";

    public static final String INVOICE_NO = "invoice_no";

    public static final String REASON = "reason";

    public static final String STATUS = "status";

    public static final String CREATE_USER = "create_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_USER = "update_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String FILE_TYPE = "file_type";

    public static final String UPLOAD_ID = "upload_id";

    public static final String UPLOAD_PATH = "upload_path";

    public static final String SETTLEMENT_NO = "settlement_no";

    public static final String PAPER_DREW_DATE = "paper_drew_date";

    public static final String AMOUNT = "amount";

    public static final String CHECK_CODE = "check_code";

    public static final String INVOICE_CODE = "invoice_code";

    public static final String FILE_NAME = "file_name";

    public static final String BUSINESS_TYPE = "business_type";

}
