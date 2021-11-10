package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import com.xforceplus.wapp.repository.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
/**
 * <p>
    * 
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-11-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_match_weekdays")
public class TXfMatchWeekdaysEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，雪花算法
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 工作日
     */
    @TableField("weekdays")
    private Date weekdays;

    /**
     * 创建时间

     */
    @TableField(value = "create_time" ,fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 创建人
     */
    @TableField(value = "create_user",fill = FieldFill.INSERT)
    private String createUser;

    /**
     * 修改时间
     */
    @TableField(value="update_time", fill = FieldFill.UPDATE,update="getdate()" )
    private Date updateTime;

    /**
     * 最后修改人
     */
    @TableField("update_user")
    private String updateUser;


    public static final String ID = "id";

    public static final String WEEKDAYS = "weekdays";

    public static final String CREATE_TIME = "create_time";

    public static final String CREATE_USER = "create_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String UPDATE_USER = "update_user";

}
