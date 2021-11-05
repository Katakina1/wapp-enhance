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
    * 供应商单据（索赔单、协议单、EPD单）及明细采集任务
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_bill_job")
public class TXfBillJobEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务名称 以扫描到的文件名作为任务名
     */
    @TableField("job_name")
    private String jobName;

    /**
     * 任务类型 1-索赔单及明细数据采集任务 2-协议单数据采集任务 3-EPD单及LOG明细数据采集i任务
     */
    @TableField("job_type")
    private Integer jobType;

    /**
     * 任务状态 0-任务失败 1-任务初始化 2-数据文件下载完成 3-原始数据采集完成 4-数据梳理录入完成 9-全部完成
     */
    @TableField("job_status")
    private Integer jobStatus;

    /**
     * 任务锁定状态，锁定代表当前任务正在被执行，不允许其他节点或线程重复执行 0-未锁定 1-已锁定
     */
    @TableField("job_lock_status")
    private Boolean jobLockStatus;

    /**
     * 原始数据采集对象 依次为 1-单据 2-单据明细（EPD单log明细或索赔单Hyper明细） 3-单据明细（索赔单Sams明细）
     */
    @TableField("job_acquisition_object")
    private Integer jobAcquisitionObject;

    /**
     * 原始数据采集进度 当前处理完成的数据行数，起始值是1（表头行）
     */
    @TableField("job_acquisition_progress")
    private Long jobAcquisitionProgress;

    /**
     * 原始数据处理对象 依次为 1-单据（job_type为协议单时代表SAP-FBL5N） 2-单据明细（job_type为EPD时代表EPD单log明细，job_type为索赔单时代表Hyper明细，job_type为协议单时代表SAP-ZARR0355原稿） 3-单据明细（索赔单Sams明细）
     */
    @TableField("job_entry_object")
    private Integer jobEntryObject;

    /**
     * 数据梳理录入进度 当前处理完成的分页数，起始值是0
     */
    @TableField("job_entry_progress")
    private Long jobEntryProgress;

    /**
     * 当前处理的提示信息 用于出现异常时指导用户操作
     */
    @TableField("remark")
    private String remark;

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
     * 协议单FBL5N合并时已处理完成的分页数，起始是0
     */
    @TableField("job_entry_agreement_fbl5n_progress ")
    private Long jobEntryAgreementFbl5nProgress ;

    /**
     * 协议单ZARR0355合并时已处理完成的分页数，起始是0
     */
    @TableField("job_entry_agreement_zarr_progress")
    private Long jobEntryAgreementZarrProgress;

    /**
     * 索赔单主信息同步到业务单页码，起始是0
     */
    @TableField("job_entry_claim_bill_progress")
    private Long jobEntryClaimBillProgress;

    /**
     * 索赔单明细hyper同步业务单页码，起始是0
     */
    @TableField("job_entry_claim_item_hyper_progress ")
    private Long jobEntryClaimItemHyperProgress;

    /**
     * 索赔单明细sams同步到业务单页码，起始是0
     */
    @TableField("job_entry_claim_item_sams_progress")
    private Long jobEntryClaimItemSamsProgress;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    public static final String JOB_NAME = "job_name";

    public static final String JOB_TYPE = "job_type";

    public static final String JOB_STATUS = "job_status";

    public static final String JOB_LOCK_STATUS = "job_lock_status";

    public static final String JOB_ACQUISITION_OBJECT = "job_acquisition_object";

    public static final String JOB_ACQUISITION_PROGRESS = "job_acquisition_progress";

    public static final String JOB_ENTRY_OBJECT = "job_entry_object";

    public static final String JOB_ENTRY_PROGRESS = "job_entry_progress";

    public static final String REMARK = "remark";

    public static final String CREATE_USER = "create_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_USER = "update_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String JOB_ENTRY_AGREEMENT_FBL5N_PROGRESS  = "job_entry_agreement_fbl5n_progress ";

    public static final String JOB_ENTRY_AGREEMENT_ZARR_PROGRESS = "job_entry_agreement_zarr_progress";

    public static final String JOB_ENTRY_CLAIM_BILL_PROGRESS = "job_entry_claim_bill_progress";

    public static final String JOB_ENTRY_CLAIM_ITEM_HYPER_PROGRESS = "job_entry_claim_item_hyper_progress ";

    public static final String JOB_ENTRY_CLAIM_ITEM_SAMS_PROGRESS = "job_entry_claim_item_sams_progress";

    public static final String ID = "id";

}
