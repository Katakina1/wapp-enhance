package com.xforceplus.wapp.modules.base.entity;


import com.xforceplus.wapp.common.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * 业务类型
 *
 * Created by Daily.zhang on 2018/04/13.
 */
@Getter
@Setter
public class AribaBillTypeEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;


    private String isdefault;

	private String rownumber;

	public String getRownumber() {
		return rownumber;
	}

	public void setRownumber(String rownumber) {
		this.rownumber = rownumber;
	}

	public String getIsdefault() {
		return isdefault;
	}

	public void setIsdefault(String isdefault) {
		this.isdefault = isdefault;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
     * 业务类型id
     */
    private Integer id;

    /**
     * 业务类型名称
     */
    private String serviceName;

    /**
     * mccCode
     */
    private String mccCode;

    /**
     * glAccount
     */
    private String glAccount;

	private Long[] ids;
	private String serviceType;

	public Integer getRow() {
		return row;
	}

	public void setRow(Integer row) {
		this.row = row;
	}

	//sheet行
	private Integer row;
	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public Long[] getIds() {
		return (ids == null) ? null : Arrays.copyOf(ids, ids.length);
	}

	public void setIds(Long[] ids) {
		this.ids = ids == null ? null : Arrays.copyOf(ids, ids.length);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMccCode() {
		return mccCode;
	}

	public void setMccCode(String mccCode) {
		this.mccCode = mccCode;
	}

	public String getGlAccount() {
		return glAccount;
	}

	public void setGlAccount(String glAccount) {
		this.glAccount = glAccount;
	}
}
