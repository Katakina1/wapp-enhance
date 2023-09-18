package com.xforceplus.wapp.repository.entity;

import java.math.BigDecimal;
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
    * 用户表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2022-09-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_ac_user")
public class TAcUserEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 人员姓名
     */
    @TableField("username")
    private String username;

    /**
     * 人员编码
     */
    @TableField("usercode")
    private String usercode;

    /**
     * 加密密码
     */
    @TableField("password")
    private String password;

    /**
     * 登录名
     */
    @TableField("loginname")
    private String loginname;

    /**
     * 性别
     */
    @TableField("sex")
    private String sex;

    /**
     * 出生日期
     */
    @TableField("birthday")
    private Date birthday;

    /**
     * 电子邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 机构id
     */
    @TableField("orgid")
    private Integer orgid;

    /**
     * 电话号码
     */
    @TableField("phone")
    private String phone;

    /**
     * 手机号码
     */
    @TableField("cellphone")
    private String cellphone;

    /**
     * 联系地址
     */
    @TableField("address")
    private String address;

    /**
     * 用户类型（供应商付款类型(0月结，1半月结)），2购销
     */
    @TableField("usertype")
    private String usertype;

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
     * 状态（0:'初始 ',1:'正常',2:'挂起 ',3:'锁定',4:'停用）
     */
    @TableField("status")
    private String status;

    /**
     * 未加密密码
     */
    @TableField("plainpassword")
    private String plainpassword;

    /**
     * 开户行
     */
    @TableField("bank_name")
    private String bankName;

    /**
     * 银行帐号
     */
    @TableField("bank_account")
    private String bankAccount;

    /**
     * 记帐起始日期类型
     */
    @TableField("invoice_aging_date")
    private String invoiceAgingDate;

    /**
     * 部门编号
     */
    @TableField("depno")
    private String depno;

    /**
     * 扩展字段1(extension field) 
     */
    @TableField("extf0")
    private String extf0;

    /**
     * 扩展字段2(extension field)
     */
    @TableField("extf1")
    private String extf1;

    /**
     * 扩展字段3(extension field)
     */
    @TableField("extf2")
    private String extf2;

    /**
     * 扩展字段4(extension field)
     */
    @TableField("extf3")
    private String extf3;

    /**
     * 扩展字段5(extension field）
     */
    @TableField("extf4")
    private String extf4;

    /**
     * 密码修改时间
     */
    @TableField("pwd_modify_time")
    private Date pwdModifyTime;

    /**
     * 密码连续错误次数
     */
    @TableField("pwd_wrong_count")
    private BigDecimal pwdWrongCount;

    /**
     * 锁定时间
     */
    @TableField("lock_time")
    private Date lockTime;

    /**
     * 部门名称
     */
    @TableField("depname")
    private String depname;

    /**
     * 区域编号
     */
    @TableField("regionno")
    private String regionno;

    /**
     * 邮政编码
     */
    @TableField("postcode")
    private String postcode;

    /**
     * 传真
     */
    @TableField("fax")
    private String fax;

    /**
     * 供应商类别
     */
    @TableField("orgtype")
    private String orgtype;

    /**
     * 银行代码
     */
    @TableField("bank_code")
    private String bankCode;

    /**
     * 业务人员
     */
    @TableField("bususername")
    private String bususername;

    /**
     * 业务人员电话/手机
     */
    @TableField("busphone")
    private String busphone;

    /**
     * 业务人员邮箱
     */
    @TableField("busemail")
    private String busemail;

    /**
     * 财务人员
     */
    @TableField("finusername")
    private String finusername;

    /**
     * 财务人员电话/手机
     */
    @TableField("finphone")
    private String finphone;

    /**
     * 财务人员邮箱
     */
    @TableField("finemail")
    private String finemail;

    /**
     * 退货是否票折,费用是否票折,生效日期(yyyy-mm-dd hh24:mi:ss),以前退货是否票折,以前费用是否票折， 0不票折,1票折
     */
    @TableField("discount")
    private String discount;

    /**
     * mycat同步标识
     */
    @TableField("_mycat_op_time")
    private Long mycatOpTime;

    /**
     * 供应商类型（-1全部 0-KEY Vendor 1-VIP Vendor 2-其他）
     */
    @TableField("org_level")
    private String orgLevel;

    /**
     * 10位供应商号
     */
    @TableField("ten_usercode")
    private String tenUsercode;

    /**
     * 城市
     */
    @TableField("city")
    private String city;

    @TableField("post_address")
    private String postAddress;

    @TableField("post_type")
    private String postType;

    @TableField("userid")
    private Integer userid;

    @TableField("serviceType")
    private Integer serviceType;

    @TableField("assertDate")
    private String assertDate;

    @TableField("expireDate")
    private String expireDate;

    @TableField("updateDate")
    private String updateDate;

    public static final String USERNAME = "username";

    public static final String USERCODE = "usercode";

    public static final String PASSWORD = "password";

    public static final String LOGINNAME = "loginname";

    public static final String SEX = "sex";

    public static final String BIRTHDAY = "birthday";

    public static final String EMAIL = "email";

    public static final String ORGID = "orgid";

    public static final String PHONE = "phone";

    public static final String CELLPHONE = "cellphone";

    public static final String ADDRESS = "address";

    public static final String USERTYPE = "usertype";

    public static final String CREATE_TIME = "create_time";

    public static final String CREATE_BY = "create_by";

    public static final String LAST_MODIFY_TIME = "last_modify_time";

    public static final String LAST_MODIFY_BY = "last_modify_by";

    public static final String STATUS = "status";

    public static final String PLAINPASSWORD = "plainpassword";

    public static final String BANK_NAME = "bank_name";

    public static final String BANK_ACCOUNT = "bank_account";

    public static final String INVOICE_AGING_DATE = "invoice_aging_date";

    public static final String DEPNO = "depno";

    public static final String EXTF0 = "extf0";

    public static final String EXTF1 = "extf1";

    public static final String EXTF2 = "extf2";

    public static final String EXTF3 = "extf3";

    public static final String EXTF4 = "extf4";

    public static final String PWD_MODIFY_TIME = "pwd_modify_time";

    public static final String PWD_WRONG_COUNT = "pwd_wrong_count";

    public static final String LOCK_TIME = "lock_time";

    public static final String DEPNAME = "depname";

    public static final String REGIONNO = "regionno";

    public static final String POSTCODE = "postcode";

    public static final String FAX = "fax";

    public static final String ORGTYPE = "orgtype";

    public static final String BANK_CODE = "bank_code";

    public static final String BUSUSERNAME = "bususername";

    public static final String BUSPHONE = "busphone";

    public static final String BUSEMAIL = "busemail";

    public static final String FINUSERNAME = "finusername";

    public static final String FINPHONE = "finphone";

    public static final String FINEMAIL = "finemail";

    public static final String DISCOUNT = "discount";

    public static final String _MYCAT_OP_TIME = "_mycat_op_time";

    public static final String ORG_LEVEL = "org_level";

    public static final String TEN_USERCODE = "ten_usercode";

    public static final String CITY = "city";

    public static final String POST_ADDRESS = "post_address";

    public static final String POST_TYPE = "post_type";

    public static final String USERID = "userid";

    public static final String SERVICETYPE = "serviceType";

    public static final String ASSERTDATE = "assertDate";

    public static final String EXPIREDATE = "expireDate";

    public static final String UPDATEDATE = "updateDate";
}
