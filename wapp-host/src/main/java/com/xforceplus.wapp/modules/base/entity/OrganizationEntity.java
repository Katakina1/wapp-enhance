package com.xforceplus.wapp.modules.base.entity;

import com.xforceplus.wapp.common.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 组织机构
 * <p>
 * Created by Daily.zhang on 2018/04/12.
 */
@Getter
@Setter
public class OrganizationEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户所在分库名
     */
    private String schemaLabel;

    public String getSchemaLabel() {
		return schemaLabel;
	}

	public void setSchemaLabel(String schemaLabel) {
		this.schemaLabel = schemaLabel;
	}

	public Integer getUserTaxnoId() {
		return userTaxnoId;
	}

	public void setUserTaxnoId(Integer userTaxnoId) {
		this.userTaxnoId = userTaxnoId;
	}

	public Long getOrgid() {
		return orgid;
	}

	public void setOrgid(Long orgid) {
		this.orgid = orgid;
	}

	public String getOrgcode() {
		return orgcode;
	}

	public void setOrgcode(String orgcode) {
		this.orgcode = orgcode;
	}

	public String getOrgname() {
		return orgname;
	}

	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}

	public String getTaxno() {
		return taxno;
	}

	public void setTaxno(String taxno) {
		this.taxno = taxno;
	}

	public String getTaxname() {
		return taxname;
	}

	public void setTaxname(String taxname) {
		this.taxname = taxname;
	}

	public Integer getParentid() {
		return parentid;
	}

	public void setParentid(Integer parentid) {
		this.parentid = parentid;
	}

	public String getOrgtype() {
		return orgtype;
	}

	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}

	public String getOrgtypeStr() {
		return orgtypeStr;
	}

	public void setOrgtypeStr(String orgtypeStr) {
		this.orgtypeStr = orgtypeStr;
	}

	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getIsbottom() {
		return isbottom;
	}

	public void setIsbottom(String isbottom) {
		this.isbottom = isbottom;
	}

	public Integer getOrglevel() {
		return orglevel;
	}

	public void setOrglevel(Integer orglevel) {
		this.orglevel = orglevel;
	}

	public String getOrglayer() {
		return orglayer;
	}

	public void setOrglayer(String orglayer) {
		this.orglayer = orglayer;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSortno() {
		return sortno;
	}

	public void setSortno(String sortno) {
		this.sortno = sortno;
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

	public Integer getComType() {
		return comType;
	}

	public void setComType(Integer comType) {
		this.comType = comType;
	}

	public Integer getIsBlack() {
		return isBlack;
	}

	public void setIsBlack(Integer isBlack) {
		this.isBlack = isBlack;
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

	public String getExtf5() {
		return extf5;
	}

	public void setExtf5(String extf5) {
		this.extf5 = extf5;
	}

	public String getExtf6() {
		return extf6;
	}

	public void setExtf6(String extf6) {
		this.extf6 = extf6;
	}

	public String getExtf7() {
		return extf7;
	}

	public void setExtf7(String extf7) {
		this.extf7 = extf7;
	}

	public String getExtf8() {
		return extf8;
	}

	public void setExtf8(String extf8) {
		this.extf8 = extf8;
	}

	public String getExtf9() {
		return extf9;
	}

	public void setExtf9(String extf9) {
		this.extf9 = extf9;
	}

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public String getIsInsert() {
		return isInsert;
	}

	public void setIsInsert(String isInsert) {
		this.isInsert = isInsert;
	}

	public Boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public Boolean getOpen() {
		return open;
	}

	public void setOpen(Boolean open) {
		this.open = open;
	}

	public List<OrganizationEntity> getChildren() {
		return children;
	}

	public void setChildren(List<OrganizationEntity> children) {
		this.children = children;
	}

	public String getOrgChildStr() {
		return orgChildStr;
	}

	public void setOrgChildStr(String orgChildStr) {
		this.orgChildStr = orgChildStr;
	}

	public String getOrgldStr() {
		return orgldStr;
	}

	public void setOrgldStr(String orgldStr) {
		this.orgldStr = orgldStr;
	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
     * 用户税号关联表id
     */
    private Integer userTaxnoId;

    /**
     * 机构id
     */
    private Long orgid;

    /**
     * 机构编码
     */
    private String orgcode;

    /**
     * 机构名称
     */
    private String orgname;

    /**
     * 纳税人识别号
     */
    private String taxno;

    /**
     * 纳税人名称
     */
    private String taxname;

    /**
     * 上级机构id
     */
    private Integer parentid;

    /**
     * 机构类型机构类型(0-大象慧云;1-中心企业;2-购方虚机构;3-销方虚机构;4-管理机构;5-购方企业;6-购销双方;7-门店;8-销方企业)
     */
    private String orgtype;

    private String orgtypeStr;

    /**
     * 联系人
     */
    private String linkman;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 联系地址
     */
    private String address;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 邮政编码
     */
    private String postcode;

    /**
     * 开户行
     */
    private String bank;

    /**
     * 银行帐号
     */
    private String account;

    /**
     * 是否有下级[０－无；１－有]
     */
    private String isbottom;

    /**
     * 机构级别
     */
    private Integer orglevel;

    /**
     * 机构层级代码
     */
    private String orglayer;

    /**
     * 所属中心企业
     */
    private String company;

    /**
     * 备注
     */
    private String remark;

    /**
     * 排序字段
     */
    private String sortno;

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
     * 公司类型 0国家，1企业
     */
    private Integer comType;

    /**
     * 是否加入黑名单  0未加入 1 已加入
     */
    private Integer isBlack;


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

    /**
     * 扩展字段6(extension field)
     */
    private String extf5;

    /**
     * 扩展字段7(extension field)
     */
    private String extf6;

    /**
     * 扩展字段8(extension field)
     */
    private String extf7;

    /**
     * 扩展字段9(extension field)
     */
    private String extf8;

    /**
     * 扩展字段10(extension field)
     */
    private String extf9;

    private Long[] orgIds;

    //数据库连接名
    private String linkName;

    //是否新增数据库(1：新增)
    private String isInsert;

    //是否为叶子节点
    private Boolean isLeaf = Boolean.FALSE;

    //是否打开节点,默认关闭
    private Boolean open = Boolean.FALSE;

    //子节点
    private List<OrganizationEntity> children;

    /**
     * 子级组织id
     */
    private String orgChildStr;

    private String orgldStr;

    private List<Long> ids;

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

    public Long[] getOrgIds() {
        return (orgIds == null) ? null : Arrays.copyOf(orgIds, orgIds.length);
    }

    public void setOrgIds(Long[] orgIds) {
        this.orgIds = orgIds == null ? null : Arrays.copyOf(orgIds, orgIds.length);
    }

}
