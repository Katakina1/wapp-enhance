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
 * @since 2021-11-10
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
     * 备注
     */
    @TableField("weekdays_remark")
    private String weekdaysRemark;

    /**
     * 创建时间

     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 修改时间
     */
    @TableField(value="update_time", update="getdate()" )
    private Date updateTime;

    /**
     * 最后修改人
     */
    @TableField("update_user")
    private String updateUser;


    public static final String ID = "id";

    public static final String WEEKDAYS = "weekdays";

    public static final String WEEKDAYS_REMARK = "weekdays_remark";

    public static final String CREATE_TIME = "create_time";

    public static final String CREATE_USER = "create_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String UPDATE_USER = "update_user";

}
