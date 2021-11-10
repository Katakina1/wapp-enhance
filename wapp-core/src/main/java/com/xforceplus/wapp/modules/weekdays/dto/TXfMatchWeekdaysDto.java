package com.xforceplus.wapp.modules.weekdays.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xforceplus.wapp.repository.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

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
@ToString(callSuper = true)
public class TXfMatchWeekdaysDto extends BaseEntity {


    /**
     * 主键，雪花算法
     */
    @ApiModelProperty("主键，雪花算法")
    private Long id;

    /**
     * 工作日
     */
    @ApiModelProperty("工作日")
    private Date weekdays;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createUser;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private Date updateTime;

    /**
     * 最后修改人
     */
    @ApiModelProperty("最后修改人")
    private String updateUser;


    public static final String ID = "id";

    public static final String WEEKDAYS = "weekdays";

    public static final String CREATE_TIME = "create_time";

    public static final String CREATE_USER = "create_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String UPDATE_USER = "update_user";

}
