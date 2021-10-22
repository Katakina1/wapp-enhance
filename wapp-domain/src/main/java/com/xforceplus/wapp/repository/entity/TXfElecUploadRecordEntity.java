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
    * 专用电票上传记录表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_elec_upload_record")
public class TXfElecUploadRecordEntity extends BaseEntity {

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
     * 上传文件的总数
     */
    @TableField("total_num")
    private Integer totalNum;

    /**
     * 成功的数量
     */
    @TableField("succeed_num")
    private Integer succeedNum;

    /**
     * 失败的数量
     */
    @TableField("failure_num")
    private Integer failureNum;

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
     * 供应商ID
     */
    @TableField("vendor_id")
    private String vendorId;

    /**
     * 子公司代码
     */
    @TableField("jv_code")
    private String jvCode;

    /**
     * 购方名称
     */
    @TableField("gf_name")
    private String gfName;


    public static final String ID = "id";

    public static final String BATCH_NO = "batch_no";

    public static final String TOTAL_NUM = "total_num";

    public static final String SUCCEED_NUM = "succeed_num";

    public static final String FAILURE_NUM = "failure_num";

    public static final String CREATE_USER = "create_user";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_USER = "update_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String VENDOR_ID = "vendor_id";

    public static final String JV_CODE = "jv_code";

    public static final String GF_NAME = "gf_name";

}
