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
@TableName(value="t_xf_invoice_file")
public class TXfInvoiceFileEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，雪花算法
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 发票号码
     */
    @TableField("invoice_no")
    private String invoiceNo;

    /**
     * 发票代码
     */
    @TableField("invoice_code")
    private String invoiceCode;

    /**
     * 文件类型,0:jpg;1:pdf;2:ofd
     */
    @TableField("type")
    private Integer type;

    /**
     * 文件路径/id
     */
    @TableField("path")
    private String path;

    /**
     * 0 沃尔玛文件服务
     */
    @TableField("storage")
    private Integer storage;

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
     * 更新时间
     */
    @TableField(value="update_time", update="getdate()" )
    private Date updateTime;

    /**
     * 更新人
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 文件来源，0 供应商上传
     */
    @TableField("origin")
    private Integer origin;

    /**
     * 1 正常，0删除
     */
    @TableField("status")
    private Integer status;

    /**
     * 供应商6d号
     */
    @TableField("vender_id")
    private String venderId;

    /**
     * 业务id
     * 费用上传填入 扫描表id
     */
    @TableField("business_id")
    private String businessId;

    /**
     * 业务类型 费用填入cost
     */
    @TableField("busines_type")
    private String businesType;

    /**
     * 文件类型 0-发票 1-附件
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 文件后缀名
     */
    @TableField("file_suffix")
    private String fileSuffix;

    /**
     * 文件名称
     */
    @TableField("file_name")
    private String fileName;


    public static final String ID = "id";

    public static final String INVOICE_NO = "invoice_no";

    public static final String INVOICE_CODE = "invoice_code";

    public static final String TYPE = "type";

    public static final String PATH = "path";

    public static final String STORAGE = "storage";

    public static final String CREATE_TIME = "create_time";

    public static final String CREATE_USER = "create_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String UPDATE_USER = "update_user";

    public static final String ORIGIN = "origin";

    public static final String STATUS = "status";

}
