package com.xforceplus.wapp.repository.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

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
@ToString(callSuper = true)
@TableName(value = "t_ac_org")
public class TAcOrgEntity extends BaseEntity {

    /**
     * 机构编码
     */
    @TableField("orgcode")
    private String orgCode;

    /**
     * 机构名称
     */
    @TableField("orgname")
    private String orgName;

    /**
     * 纳税人识别号
     */
    @TableField("taxno")
    private String taxNo;

    /**
     * 纳税人名称
     */
    @TableField("taxname")
    private String taxName;

    /**
     * 上级机构id
     */
    @TableField("parentid")
    private Integer parentId;

    /**
     * 机构类型机构类型(0-大象慧云;1-中心企业;2-购方虚机构;3-销方虚机构;4-管理机构;5-购方企业;6-购销双方;7-门店;8-销方企业)
     */
    @TableField("orgtype")
    private String orgType;

    /**
     * 联系人
     */
    @TableField("linkman")
    private String linkMan;

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
    private String postCode;

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
    private String isBottom;

    /**
     * 机构级别
     */
    @TableField("orglevel")
    private Double orgLevel;

    /**
     * 机构层级代码
     */
    @TableField("orglayer")
    private String orgLayer;

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
    private String sortNo;

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
    private Long orgId;

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

    /**
     * 税盘类型
     */
    @TableField("tax_device_type")
    private Integer taxDeviceType;
    
    /**
     * 折让率
     */
    @TableField("discount_rate")
    private BigDecimal discountRate;

    public static final String ORG_CODE = "orgCode";

    public static final String ORG_TYPE = "orgType";

    public static final String ORG_NAME = "orgType";

    public static final String ORG_ID = "orgId";

    public static final String TAX_NO = "taxno";

    public static final String ACCOUNT = "account";

    public static final String BANK = "bank";

    public static final String QUOTA = "quota";

    public static final String TAX_DEVICE_TYPE ="tax_device_type";


}
