package com.xforceplus.wapp.modules.base.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Daily.zhang on 2018/04/19.
 */
@Getter
@Setter
public class UserBilltypeEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String billtypeid;
    
    private String isdefault;
    
    
   

	public String getIsdefault() {
		return isdefault;
	}

	public void setIsdefault(String isdefault) {
		this.isdefault = isdefault;
	}

	public String getBilltypeid() {
		return billtypeid;
	}

	public void setBilltypeid(String billtypeid) {
		this.billtypeid = billtypeid;
	}

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

	private String billtypename;

	private String billtypecode;

	public String getBilltypename() {
		return billtypename;
	}

	public void setBilltypename(String billtypename) {
		this.billtypename = billtypename;
	}

	public String getBilltypecode() {
		return billtypecode;
	}

	public void setBilltypecode(String billtypecode) {
		this.billtypecode = billtypecode;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public Integer getOrgid() {
		return orgid;
	}

	public void setOrgid(Integer orgid) {
		this.orgid = orgid;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getSubOrgIdStr() {
		return subOrgIdStr;
	}

	public void setSubOrgIdStr(String subOrgIdStr) {
		this.subOrgIdStr = subOrgIdStr;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	

    /**
     * 所属中心企业
     */
    private String company;

    /**
     * 用户税号关联表id
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer userid;

    /**
     * 机构id
     */
    private Integer orgid;

    /**
     * 机构父级id
     */
    private Integer parentId;

    /**
     * 子级机构id
     */
    private String subOrgIdStr;
    
    private Long[] ids;

    public Long[] getIds() {
        return (ids == null) ? null : Arrays.copyOf(ids, ids.length);
    }

    public void setIds(Long[] ids) {
        this.ids = ids == null ? null : Arrays.copyOf(ids, ids.length);
    }
}
