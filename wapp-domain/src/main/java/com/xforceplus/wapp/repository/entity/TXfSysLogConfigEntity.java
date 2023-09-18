package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xforceplus.wapp.repository.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
/**
 * <p>
    * 系统日志配置表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2022-09-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_sys_log_config")
public class TXfSysLogConfigEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 配置KEY
     */
    @TableField("config_key")
    private String configKey;

    /**
     * 配置VALUE
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 配置备注
     */
    @TableField("config_remark")
    private String configRemark;


    public static final String ID = "id";

    public static final String CONFIG_KEY = "config_key";

    public static final String CONFIG_VALUE = "config_value";

    public static final String CONFIG_REMARK = "config_remark";

}
