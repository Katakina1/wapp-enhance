package com.xforceplus.wapp.modules.base.entity;


import java.io.Serializable;
import java.util.Date;

import com.xforceplus.wapp.common.utils.DateUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 票据类型
 *
 * Created by Daily.zhang on 2018/04/13.
 */
@Getter
@Setter
public class BillTypeEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;


    private String isdefault;
    
   
	
	public String getIsdefault() {
		return isdefault;
	}

	public void setIsdefault(String isdefault) {
		this.isdefault = isdefault;
	}

	private String userBilltypeId;
    public String getUserBilltypeId() {
		return userBilltypeId;
	}

	public void setUserBilltypeId(String userBilltypeId) {
		this.userBilltypeId = userBilltypeId;
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

	public Integer getBilltypeid() {
		return billtypeid;
	}

	public void setBilltypeid(Integer billtypeid) {
		this.billtypeid = billtypeid;
	}

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

	public Integer getOrgid() {
		return orgid;
	}

	public void setOrgid(Integer orgid) {
		this.orgid = orgid;
	}

	public String getBilltypedesc() {
		return billtypedesc;
	}

	public void setBilltypedesc(String billtypedesc) {
		this.billtypedesc = billtypedesc;
	}

	public String getBilltypetype() {
		return billtypetype;
	}

	public void setBilltypetype(String billtypetype) {
		this.billtypetype = billtypetype;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
     * 票据类型id
     */
    private Integer billtypeid;

    /**
     * 票据类型名称
     */
    private String billtypename;

    /**
     * 票据类型编码
     */
    private String billtypecode;

    /**
     * 所属机构
     */
    private Integer orgid;

    /**
     * 票据类型描述
     */
    private String billtypedesc;

    /**
     * 票据类型类型
     */
    private String billtypetype;

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
    
}
