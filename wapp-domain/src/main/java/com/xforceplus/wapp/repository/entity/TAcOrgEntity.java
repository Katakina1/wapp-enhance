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
    * 机构表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_ac_org")
public class TAcOrgEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 机构编码
     */
    @TableField("orgcode")
    private String orgcode;

    /**
     * 机构名称
     */
    @TableField("orgname")
    private String orgname;

    /**
     * 纳税人识别号
     */
    @TableField("taxno")
    private String taxno;

    /**
     * 纳税人名称
     */
    @TableField("taxname")
    private String taxname;

    /**
     * 上级机构id
     */
    @TableField("parentid")
    private Integer parentid;

    /**
     * 机构类型机构类型(0-大象慧云;1-中心企业;2-购方虚机构;3-销方虚机构;4-管理机构;5-购方企业;6-购销双方;7-门店;8-销方企业)
     */
    @TableField("orgtype")
    private String orgtype;

    /**
     * 联系人
     */
    @TableField("linkman")
    private String linkman;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 联系地址
     */
    @TableField("address")
    private String address;

    /**
     * 电子邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 邮政编码
     */
    @TableField("postcode")
    private String postcode;

    /**
     * 开户行
     */
    @TableField("bank")
    private String bank;

    /**
     * 银行帐号
     */
    @TableField("account")
    private String account;

    /**
     * 是否有下级[０－无；１－有]
     */
    @TableField("isbottom")
    private String isbottom;

    /**
     * 机构级别
     */
    @TableField("orglevel")
    private Double orglevel;

    /**
     * 机构层级代码
     */
    @TableField("orglayer")
    private String orglayer;

    /**
     * 所属中心企业
     */
    @TableField("company")
    private String company;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 排序字段
     */
    @TableField("sortno")
    private String sortno;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 创建人
     */
    @TableField("create_by")
    private String createBy;

    /**
     * 修改时间
     */
    @TableField("last_modify_time")
    private Date lastModifyTime;

    /**
     * 修改人
     */
    @TableField("last_modify_by")
    private String lastModifyBy;

    /**
     * jvcode
     */
    @TableField("extf0")
    private String extf0;

    /**
     * 税务承担店号
     */
    @TableField("extf1")
    private String extf1;

    /**
     * 税务承担店名称
     */
    @TableField("extf2")
    private String extf2;

    /**
     * 税务承担店税号
     */
    @TableField("extf3")
    private String extf3;

    /**
     * 扩展字段5(extension field) 
     */
    @TableField("extf4")
    private String extf4;

    /**
     * 扩展字段6(extension field)    
     */
    @TableField("extf5")
    private String extf5;

    /**
     * 扩展字段7(extension field)   
     */
    @TableField("extf6")
    private String extf6;

    /**
     * 扩展字段8(extension field)    
     */
    @TableField("extf7")
    private String extf7;

    /**
     * 扩展字段9(extension field) 
     */
    @TableField("extf8")
    private String extf8;

    /**
     * 扩展字段10(extension field)  
     */
    @TableField("extf9")
    private String extf9;

    /**
     * 公司类型 0国家，1企业
     */
    @TableField("com_type")
    private String comType;

    /**
     * 是否加入黑名单  0未加入 1 已加入
     */
    @TableField("is_black")
    private String isBlack;

    /**
     * 开票限额
     */
    @TableField("quota")
    private Double quota;

    @TableField("orgid")
    private Integer orgid;

    @TableField("is_mm")
    private String isMm;

    @TableField("store_number")
    private String storeNumber;

    @TableField("link_name")
    private String linkName;

    @TableField("is_update")
    private String isUpdate;

    @TableField("company_code")
    private String companyCode;

    @TableField("dq_code")
    private String dqCode;


    public static final String ORGCODE = "orgcode";

    public static final String ORGNAME = "orgname";

    public static final String TAXNO = "taxno";

    public static final String TAXNAME = "taxname";

    public static final String PARENTID = "parentid";

    public static final String ORGTYPE = "orgtype";

    public static final String LINKMAN = "linkman";

    public static final String PHONE = "phone";

    public static final String ADDRESS = "address";

    public static final String EMAIL = "email";

    public static final String POSTCODE = "postcode";

    public static final String BANK = "bank";

    public static final String ACCOUNT = "account";

    public static final String ISBOTTOM = "isbottom";

    public static final String ORGLEVEL = "orglevel";

    public static final String ORGLAYER = "orglayer";

    public static final String COMPANY = "company";

    public static final String REMARK = "remark";

    public static final String SORTNO = "sortno";

    public static final String CREATE_TIME = "create_time";

    public static final String CREATE_BY = "create_by";

    public static final String LAST_MODIFY_TIME = "last_modify_time";

    public static final String LAST_MODIFY_BY = "last_modify_by";

    public static final String EXTF0 = "extf0";

    public static final String EXTF1 = "extf1";

    public static final String EXTF2 = "extf2";

    public static final String EXTF3 = "extf3";

    public static final String EXTF4 = "extf4";

    public static final String EXTF5 = "extf5";

    public static final String EXTF6 = "extf6";

    public static final String EXTF7 = "extf7";

    public static final String EXTF8 = "extf8";

    public static final String EXTF9 = "extf9";

    public static final String COM_TYPE = "com_type";

    public static final String IS_BLACK = "is_black";

    public static final String QUOTA = "quota";

    public static final String ORGID = "orgid";

    public static final String IS_MM = "is_mm";

    public static final String STORE_NUMBER = "store_number";

    public static final String LINK_NAME = "link_name";

    public static final String IS_UPDATE = "is_update";

    public static final String COMPANY_CODE = "company_code";

    public static final String DQ_CODE = "dq_code";

}
