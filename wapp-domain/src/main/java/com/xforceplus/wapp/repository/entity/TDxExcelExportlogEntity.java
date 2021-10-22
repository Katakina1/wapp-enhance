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
 * @since 2021-10-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_dx_excel_exportlog")
public class TDxExcelExportlogEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 操作账户
     */
    @TableField("user_account")
    private String userAccount;

    /**
     * 操作人
     */
    @TableField("user_name")
    private String userName;

    /**
     * 导出条件
     */
    @TableField("conditions")
    private String conditions;

    /**
     * 申请时间
     */
    @TableField("create_date")
    private Date createDate;

    /**
     * 业务唯一标识，用于适配执行的业务类
     */
    @TableField("service_type")
    private Long serviceType;

    /**
     * 文件的导出存储路径
     */
    @TableField("filepath")
    private String filepath;

    /**
     * 导出状态（0未导出，1正在导出，2已导出，3导出出错）
     */
    @TableField("export_status")
    private String exportStatus;

    /**
     * 导出开始时间
     */
    @TableField("start_date")
    private Date startDate;

    /**
     * 导出结束时间
     */
    @TableField("end_date")
    private Date endDate;

    /**
     * 导出错误信息，导出任务出错时填写
     */
    @TableField("errmsg")
    private String errmsg;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    public static final String USER_ACCOUNT = "user_account";

    public static final String USER_NAME = "user_name";

    public static final String CONDITIONS = "conditions";

    public static final String CREATE_DATE = "create_date";

    public static final String SERVICE_TYPE = "service_type";

    public static final String FILEPATH = "filepath";

    public static final String EXPORT_STATUS = "export_status";

    public static final String START_DATE = "start_date";

    public static final String END_DATE = "end_date";

    public static final String ERRMSG = "errmsg";

    public static final String ID = "id";

}
