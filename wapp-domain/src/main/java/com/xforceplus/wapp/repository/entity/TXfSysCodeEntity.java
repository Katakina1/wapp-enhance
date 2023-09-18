package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * <p>
    * 系统小代码表（可存些基础配置）
    * </p>
 *
 * @author hujintao
 * @since 2022-10-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_sys_code")
public class TXfSysCodeEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 代码id
     */
    @TableField("sys_id")
    private String sysId;

    /**
     * 代码code
     */
    @TableField("sys_code")
    private String sysCode;

    /**
     * 代码name
     */
    @TableField("sys_name")
    private String sysName;

    /**
     * 序号
     */
    @TableField("seq_num")
    private String seqNum;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    public static final String ID = "id";
    public static final String SYS_ID = "sys_id";
    public static final String SYS_CODE = "sys_code";
    public static final String SYS_NAME = "sys_name";
    public static final String SEQ_NUM = "seq_num";
    public static final String REMARK = "remark";

}
