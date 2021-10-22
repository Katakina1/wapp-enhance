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
    * 红票蓝冲关联表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_blue_relation")
public class TXfBlueRelationEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，雪花算法
     */
    @TableField("id")
    private Long id;

    /**
     * 蓝票发票号码
     */
    @TableField("blue_invoice_no")
    private String blueInvoiceNo;

    /**
     * 蓝票发票代码
     */
    @TableField("blue_invoice_code")
    private String blueInvoiceCode;

    /**
     * 红票发票号码
     */
    @TableField("red_invoice_no")
    private String redInvoiceNo;

    /**
     * 红字发票代码
     */
    @TableField("red_invoice_code")
    private String redInvoiceCode;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value="update_time", update="getdate()" )
    private Date updateTime;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 更新人
     */
    @TableField("update_user")
    private String updateUser;


    public static final String ID = "id";

    public static final String BLUE_INVOICE_NO = "blue_invoice_no";

    public static final String BLUE_INVOICE_CODE = "blue_invoice_code";

    public static final String RED_INVOICE_NO = "red_invoice_no";

    public static final String RED_INVOICE_CODE = "red_invoice_code";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String CREATE_USER = "create_user";

    public static final String UPDATE_USER = "update_user";

}
