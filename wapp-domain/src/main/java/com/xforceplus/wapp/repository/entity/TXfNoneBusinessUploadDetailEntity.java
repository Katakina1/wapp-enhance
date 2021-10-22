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
    * 非商电票上传记录明细表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_none_business_upload_detail")
public class TXfNoneBusinessUploadDetailEntity extends BaseEntity {

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
     * 发票代码
     */
    @TableField("invoice_code")
    private String invoiceCode;

    /**
     * 失败原因
     */
    @TableField("reason")
    private String reason;

    /**
     * ofd验签状态 0 验真中 1验真成功 2 验真失败
     */
    @TableField("verify_status")
    private String verifyStatus;

    /**
     * ofd验签状态 0 验签中 1验签成功 2 验签失败
     */
    @TableField("ofd_status")
    private String ofdStatus;

    /**
     * 发票上传门店
     */
    @TableField("invoice_store_no")
    private String invoiceStoreNo;

    /**
     * 门店号
     */
    @TableField("store_no")
    private String storeNo;

    /**
     * 货物/服务发生期间
     */
    @TableField("store_date")
    private String storeDate;

    /**
     * 业务类型  0 水电费 1 leasing in  2 固定资产转移
     */
    @TableField("bussiness_type")
    private String bussinessType;

    /**
     * 业务单号
     */
    @TableField("bussiness_no")
    private String bussinessNo;

    /**
     * 发票类型 1 否 2 SGA 3 IC 4 EC 5 RE 6 SR
     */
    @TableField("invoice_type")
    private String invoiceType;

    /**
     * 源文件服务返回-文件ID
     */
    @TableField("source_upload_id")
    private String sourceUploadId;

    /**
     * 源文件服务返回-文件路径
     */
    @TableField("source_upload_path")
    private String sourceUploadPath;

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
    private String fileType;

    /**
     * 是否提交 0未提交 1 已提交
     */
    @TableField("submit_flag")
    private String submitFlag;

    @TableField("invoice_date")
    private String invoiceDate;


    public static final String ID = "id";

    public static final String BATCH_NO = "batch_no";

    public static final String XF_DISCERN_TASK_ID = "xf_discern_task_id";

    public static final String XF_VERIFY_TASK_ID = "xf_verify_task_id";

    public static final String INVOICE_NO = "invoice_no";

    public static final String INVOICE_CODE = "invoice_code";

    public static final String REASON = "reason";

    public static final String VERIFY_STATUS = "verify_status";

    public static final String OFD_STATUS = "ofd_status";

    public static final String INVOICE_STORE_NO = "invoice_store_no";

    public static final String STORE_NO = "store_no";

    public static final String STORE_DATE = "store_date";

    public static final String BUSSINESS_TYPE = "bussiness_type";

    public static final String BUSSINESS_NO = "bussiness_no";

    public static final String INVOICE_TYPE = "invoice_type";

    public static final String SOURCE_UPLOAD_ID = "source_upload_id";

    public static final String SOURCE_UPLOAD_PATH = "source_upload_path";

    public static final String UPLOAD_ID = "upload_id";

    public static final String UPLOAD_PATH = "upload_path";

    public static final String CREATE_USER = "create_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_USER = "update_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String FILE_TYPE = "file_type";

    public static final String SUBMIT_FLAG = "submit_flag";

    public static final String INVOICE_DATE = "invoice_date";

}
