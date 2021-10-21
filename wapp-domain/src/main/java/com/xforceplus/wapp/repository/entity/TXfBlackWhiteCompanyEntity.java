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
    * 黑白名单
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_xf_black_white_company")
public class TXfBlackWhiteCompanyEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，雪花算法
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 公司名称
     */
    @TableField("company_name")
    private String companyName;

    /**
     * 供应商6D编号
     */
    @TableField("supplier_6D")
    private String supplier6d;

    /**
     * 供应商6D税号
     */
    @TableField("supplier_taxNo")
    private String supplierTaxno;

    /**
     * sap编号
     */
    @TableField("sap_no")
    private String sapNo;

    /**
     * 供应商类型 0 黑名单 1 白名单
     */
    @TableField("supplier_type")
    private String supplierType;

    /**
     * 开启时间
     */
    @TableField("open_date")
    private Date openDate;

    /**
     * 结束时间
     */
    @TableField("close_date")
    private Date closeDate;

    /**
     * 是否有效 0：有效 1：无效
     */
    @TableField("supplier_status")
    private String supplierStatus;

    /**
     * createTime
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * createUser
     */
    @TableField("create_user")
    private String createUser;

    /**
     * updateTime
     */
    @TableField(value="update_time", update="getdate()" )
    private Date updateTime;

    /**
     * updateUser
     */
    @TableField("update_user")
    private String updateUser;


    public static final String ID = "id";

    public static final String COMPANY_NAME = "company_name";

    public static final String SUPPLIER_6D = "supplier_6D";

    public static final String SUPPLIER_TAXNO = "supplier_taxNo";

    public static final String SAP_NO = "sap_no";

    public static final String SUPPLIER_TYPE = "supplier_type";

    public static final String OPEN_DATE = "open_date";

    public static final String CLOSE_DATE = "close_date";

    public static final String SUPPLIER_STATUS = "supplier_status";

    public static final String CREATE_TIME = "create_time";

    public static final String CREATE_USER = "create_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String UPDATE_USER = "update_user";

}
