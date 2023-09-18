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
    * 系统日志表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2022-09-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_sys_log")
public class TXfSysLogEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 模块代码
     */
    @TableField("module_code")
    private String moduleCode;

    /**
     * 模块名称
     */
    @TableField("module_name")
    private String moduleName;

    /**
     * 场景代码
     */
    @TableField("scene_code")
    private String sceneCode;

    /**
     * 线程名
     */
    @TableField("thread_name")
    private String threadName;

    /**
     * 业务ID
     */
    @TableField("business_id")
    private String businessId;

    /**
     * 业务状态
     */
    @TableField("business_status")
    private String businessStatus;

    /**
     * 业务属性扩展1
     */
    @TableField("business_ext1")
    private String businessExt1;

    /**
     * 业务属性扩展2
     */
    @TableField("business_ext2")
    private String businessExt2;

    /**
     * 业务属性扩展3
     */
    @TableField("business_ext3")
    private String businessExt3;

    /**
     * 业务日志
     */
    @TableField("business_log")
    private String businessLog;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;


    public static final String ID = "id";

    public static final String MODULE_CODE = "module_code";

    public static final String MODULE_NAME = "module_name";

    public static final String SCENE_CODE = "scene_code";

    public static final String THREAD_NAME = "thread_name";

    public static final String BUSINESS_ID = "business_id";

    public static final String BUSINESS_STATUS = "business_status";

    public static final String BUSINESS_EXT1 = "business_ext1";

    public static final String BUSINESS_EXT2 = "business_ext2";

    public static final String BUSINESS_EXT3 = "business_ext3";

    public static final String BUSINESS_LOG = "business_log";

    public static final String CREATE_TIME = "create_time";

}
