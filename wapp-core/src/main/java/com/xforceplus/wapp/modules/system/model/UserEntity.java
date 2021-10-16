package com.xforceplus.wapp.modules.system.model;

import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.repository.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Created by Daily.zhang on 2018/04/16.
 */
@Getter
@Setter
public class UserEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户所在分库名
     */
    private String schemaLabel;

    /**
     * 所属中心企业
     */
    private String company;

    /**
     * 用户表id
     */
    private Integer userid;

    /**
     * 人员姓名
     */
    private String username;

    /**
     * 人员编码
     */
    private String usercode;

    /**
     * 10位供应商号
     */
    private String tenUserCode;

    /**
     * 加密密码
     */
    private String password;

    /**
     * 登录名
     */
    private String loginname;

    /**
     * 性别
     */
    private String sex;

    /**
     * 出生日期
     */
    private Date birthday;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 机构id
     */
    private Integer orgid;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 手机号码
     */
    private String cellphone;

    /**
     * 联系地址
     */
    private String address;

    /**
     * 用户类型（莲花专柜供应商付款类型(0月结，1半月结)），2购销
     */
    private String usertype;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 修改时间
     */
    private Date lastModifyTime;

    /**
     * 修改人
     */
    private String lastModifyBy;

    /**
     * 状态
     */
    private String status;

    /**
     * 未加密密码
     */
    private String plainpassword;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 银行帐号
     */
    private String bankAccount;

    /**
     * 记帐起始日期类型
     */
    private String invoiceAgingDate;

    /**
     * 部门编号
     */
    private String depno;

    /**
     * 密码修改时间
     */
    private Date pwdModifyTime;

    /**
     * 密码连续错误次数
     */
    private Integer pwdWrongCount;

    /**
     * 锁定时间
     */
    private Date lockTime;

    /**
     * 部门名称
     */
    private String depname;

    /**
     * 区域编号
     */
    private String regionno;

    /**
     * 邮政编码
     */
    private String postcode;

    /**
     * 传真
     */
    private String fax;

    /**
     * 供应商类别
     */
    private String orgtype;

    /**
     * 银行代码
     */
    private String bankCode;

    /**
     * 业务人员
     */
    private String bususername;

    /**
     * 业务人员电话/手机
     */
    private String busphone;

    /**
     * 业务人员邮箱
     */
    private String busemail;

    /**
     * 财务人员
     */
    private String finusername;

    /**
     * 财务人员电话/手机
     */
    private String finphone;

    /**
     * 财务人员邮箱
     */
    private String finemail;

    /**
     * 退货是否票折,费用是否票折,生效日期(yyyy-mm-dd hh24:mi:ss),以前退货是否票折,以前费用是否票折， 0不票折,1票折
     */
    private String discount;

    /**
     * 扩展字段1(extension field)
     */
    private String extf0;

    /**
     * 扩展字段2(extension field)
     */
    private String extf1;

    /**
     * 扩展字段3(extension field)
     */
    private String extf2;

    /**
     * 扩展字段4(extension field)
     */
    private String extf3;

    /**
     * 扩展字段5(extension field)
     */
    private String extf4;

    //组织名称
    private String orgName;

    //角色id
    private Long roleId;

    private String bind;

    private List<Long> ids;

    private String orgIdStr;

    //供应商列表
    private List<UserEntity> userList;

    //供应商号
    private String[] venderId;

    private Long announcementid;//公告表关联id

    //是否同意参加培训
    private String isAgree;
    
    

    /**
     * 扩展字段6(extension field)
     */
    private String orgLevel;
    
    private String expireTime;
    @Override
    public String toString() {
        return toStringHelper(this).
                add("company", company).
                add("userid", userid).
                add("username", username).
                add("usercode", usercode).
                add("password", password).
                add("loginname", loginname).
                add("sex", sex).
                add("birthday", birthday).
                add("email", email).
                add("orgid", orgid).
                add("phone", phone).
                add("cellphone", cellphone).
                add("address", address).
                add("usertype", usertype).
                add("createTime", createTime).
                add("createBy", createBy).
                add("lastModifyTime", lastModifyTime).
                add("lastModifyBy", lastModifyBy).
                add("status", status).
                add("plainpassword", plainpassword).
                add("bankName", bankName).
                add("bankAccount", bankAccount).
                add("invoiceAgingDate", invoiceAgingDate).
                add("depno", depno).
                add("pwdModifyTime", pwdModifyTime).
                add("pwdWrongCount", pwdWrongCount).
                add("lockTime", lockTime).
                add("depname", depname).
                add("regionno", regionno).
                add("postcode", postcode).
                add("fax", fax).
                add("orgtype", orgtype).
                add("bankCode", bankCode).
                add("bususername", bususername).
                add("busphone", busphone).
                add("busemail", busemail).
                add("finusername", finusername).
                add("finphone", finphone).
                add("finemail", finemail).
                add("discount", discount).
                add("extf0", extf0).
                add("extf1", extf1).
                add("extf2", extf2).
                add("extf3", extf3).
                add("extf4", extf4).
                add("orgName", orgName).
                add("roleId", roleId).
                add("bind", bind).
                add("schemaLabel", schemaLabel).
                add("orgLevel", orgLevel).
                toString();
    }

    public Date getBirthday() {
        return DateUtils.obtainValidDate(this.birthday);
    }

    public void setBirthday(Date birthday) {
        this.birthday = DateUtils.obtainValidDate(birthday);
    }

    public Date getCreateTime() {
        return DateUtils.obtainValidDate(this.createTime);
    }

    public void setCreateTime(Date createTime) {
        this.createTime = DateUtils.obtainValidDate(createTime);
    }

    public Date getLastModifyTime() {
        return DateUtils.obtainValidDate(this.lastModifyTime);
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = DateUtils.obtainValidDate(lastModifyTime);
    }

    public Date getPwdModifyTime() {
        return DateUtils.obtainValidDate(this.pwdModifyTime);
    }

    public void setPwdModifyTime(Date pwdModifyTime) {
        this.pwdModifyTime = DateUtils.obtainValidDate(pwdModifyTime);
    }

    public Date getLockTime() {
        return DateUtils.obtainValidDate(this.lockTime);
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = DateUtils.obtainValidDate(lockTime);
    }

    public String getOrgLevel() {
        return orgLevel;
    }

    public void setOrgLevel(String orgLevel) {
        this.orgLevel = orgLevel;
    }
}
