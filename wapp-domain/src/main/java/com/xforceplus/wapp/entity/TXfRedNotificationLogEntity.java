package com.xforceplus.wapp.entity;

import com.xforceplus.wapp.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
/**
 * <p>
    * 操作流水表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_red_notification_log")
public class TXfRedNotificationLogEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 红字信息表id
     */
    @TableField("apply_id")
    private Long applyId;

    /**
     * 操作流水状态 1处理中，2，处理成功，3处理失败
     */
    @TableField("status")
    private Integer status;

    /**
     * 操作流水备注
     */
    @TableField("process_remark")
    private String processRemark;

    /**
     * 设备唯一编码
     */
    @TableField("device_un")
    private String deviceUn;

    /**
     * 终端唯一编码
     */
    @TableField("terminal_un")
    private String terminalUn;

    /**
     * 操作类型  1 申请 2 同步 3 撤销 4 删除
     */
    @TableField("apply_type")
    private Integer applyType;

    /**
     * 请求流水号
     */
    @TableField("serial_no")
    private String serialNo;

    /**
     * 操作人id
     */
    @TableField("create_user_id")
    private Long createUserId;

    /**
     * 创建时间
     */
    @TableField("create_date")
    private Date createDate;

    /**
     * 更新时间
     */
    @TableField("update_date")
    private Date updateDate;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    public static final String APPLY_ID = "apply_id";

    public static final String STATUS = "status";

    public static final String PROCESS_REMARK = "process_remark";

    public static final String DEVICE_UN = "device_un";

    public static final String TERMINAL_UN = "terminal_un";

    public static final String APPLY_TYPE = "apply_type";

    public static final String SERIAL_NO = "serial_no";

    public static final String CREATE_USER_ID = "create_user_id";

    public static final String CREATE_DATE = "create_date";

    public static final String UPDATE_DATE = "update_date";

    public static final String ID = "id";

}
