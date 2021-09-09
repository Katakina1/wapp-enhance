package com.xforceplus.wapp.modules.base.entity;

import com.xforceplus.wapp.common.utils.DateUtils;
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
    private String corporation;

	public String getCorporation() {
		return corporation;
	}

	public void setCorporation(String corporation) {
		this.corporation = corporation;
	}

	public String getSchemaLabel() {
		return schemaLabel;
	}

	public void setSchemaLabel(String schemaLabel) {
		this.schemaLabel = schemaLabel;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getOrgid() {
		return orgid;
	}

	public void setOrgid(Integer orgid) {
		this.orgid = orgid;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getLastModifyBy() {
		return lastModifyBy;
	}

	public void setLastModifyBy(String lastModifyBy) {
		this.lastModifyBy = lastModifyBy;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPlainpassword() {
		return plainpassword;
	}

	public void setPlainpassword(String plainpassword) {
		this.plainpassword = plainpassword;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getInvoiceAgingDate() {
		return invoiceAgingDate;
	}

	public void setInvoiceAgingDate(String invoiceAgingDate) {
		this.invoiceAgingDate = invoiceAgingDate;
	}

	public String getDepno() {
		return depno;
	}

	public void setDepno(String depno) {
		this.depno = depno;
	}

	public Integer getPwdWrongCount() {
		return pwdWrongCount;
	}

	public void setPwdWrongCount(Integer pwdWrongCount) {
		this.pwdWrongCount = pwdWrongCount;
	}

	public String getDepname() {
		return depname;
	}

	public void setDepname(String depname) {
		this.depname = depname;
	}

	public String getRegionno() {
		return regionno;
	}

	public void setRegionno(String regionno) {
		this.regionno = regionno;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getOrgtype() {
		return orgtype;
	}

	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBususername() {
		return bususername;
	}

	public void setBususername(String bususername) {
		this.bususername = bususername;
	}

	public String getBusphone() {
		return busphone;
	}

	public void setBusphone(String busphone) {
		this.busphone = busphone;
	}

	public String getBusemail() {
		return busemail;
	}

	public void setBusemail(String busemail) {
		this.busemail = busemail;
	}

	public String getFinusername() {
		return finusername;
	}

	public void setFinusername(String finusername) {
		this.finusername = finusername;
	}

	public String getFinphone() {
		return finphone;
	}

	public void setFinphone(String finphone) {
		this.finphone = finphone;
	}

	public String getFinemail() {
		return finemail;
	}

	public void setFinemail(String finemail) {
		this.finemail = finemail;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getExtf0() {
		return extf0;
	}

	public void setExtf0(String extf0) {
		this.extf0 = extf0;
	}

	public String getExtf1() {
		return extf1;
	}

	public void setExtf1(String extf1) {
		this.extf1 = extf1;
	}

	public String getExtf2() {
		return extf2;
	}

	public void setExtf2(String extf2) {
		this.extf2 = extf2;
	}

	public String getExtf3() {
		return extf3;
	}

	public void setExtf3(String extf3) {
		this.extf3 = extf3;
	}

	public String getExtf4() {
		return extf4;
	}

	public void setExtf4(String extf4) {
		this.extf4 = extf4;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getBind() {
		return bind;
	}

	public void setBind(String bind) {
		this.bind = bind;
	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public String getOrgIdStr() {
		return orgIdStr;
	}

	public void setOrgIdStr(String orgIdStr) {
		this.orgIdStr = orgIdStr;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

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
}
