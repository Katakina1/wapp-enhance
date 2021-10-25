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
    * 
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_operation_log")
public class TXfOperationLogEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableField("id")
    private Long id;

    /**
     * 操作类型 0 settlement, 1 deduct ,2 invoice
     */
    @TableField("operate_type")
    private String operateType;

    /**
     * 操作描述
     */
    @TableField("operate_desc")
    private String operateDesc;

    /**
     * 操作用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 操作用户名称
     */
    @TableField("user_name")
    private String userName;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 业务状态
     */
    @TableField("business_status")
    private String businessStatus;

    /**
     * 业务id
     */
    @TableField("business_id")
    private Long businessId;


    public static final String ID = "id";

    public static final String OPERATE_TYPE = "operate_type";

    public static final String OPERATE_DESC = "operate_desc";

    public static final String USER_ID = "user_id";

    public static final String USER_NAME = "user_name";

    public static final String CREATE_TIME = "create_time";

    public static final String BUSINESS_STATUS = "business_status";

    public static final String BUSINESS_ID = "business_id";

}
