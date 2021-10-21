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
@TableName(value="t_dx_messagecontrol")
public class TDxMessagecontrolEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("user_account")
    private String userAccount;

    @TableField(value="update_time", update="getdate()" )
    private Date updateTime;

    @TableField("content")
    private String content;

    @TableField("operation_status")
    private String operationStatus;

    @TableField("attr1")
    private String attr1;

    @TableField("url")
    private String url;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("attr2")
    private String attr2;

    @TableField("title")
    private String title;

    @TableField("create_time")
    private Date createTime;


    public static final String USER_ACCOUNT = "user_account";

    public static final String UPDATE_TIME = "update_time";

    public static final String CONTENT = "content";

    public static final String OPERATION_STATUS = "operation_status";

    public static final String ATTR1 = "attr1";

    public static final String URL = "url";

    public static final String ID = "id";

    public static final String ATTR2 = "attr2";

    public static final String TITLE = "title";

    public static final String CREATE_TIME = "create_time";

}
