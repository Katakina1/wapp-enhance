package com.xforceplus.wapp.modules.base.entity;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 扫描点
 *
 * Created by Daily.zhang on 2018/04/13.
 */

public class UserScanPathEntity extends BaseEntity implements Serializable {
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
    /**
	 * 扫描配置id
	 */
	private Long id;
	
	/**
	 * 扫描配置路径
	 */
	private String scanPath;
	
	
	private String invoiceRemark;
	
	/**
	 * 用户id
	 */
	private String userId;
	
	/**
	 * 关联表的主键
	 */
	
	private String uuid;
	
	private String scanId;

	/**
	 * 运营性质
	 */
	private String profit;
	
	private String pushErp;

	public String getProfit() {
		return profit;
	}

	public void setProfit(String profit) {
		this.profit = profit;
	}

	

	public String getScanPath() {
		return scanPath;
	}

	public void setScanPath(String scanPath) {
		this.scanPath = scanPath;
	}

	

	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getInvoiceRemark() {
		return invoiceRemark;
	}

	public void setInvoiceRemark(String invoiceRemark) {
		this.invoiceRemark = invoiceRemark;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getScanId() {
		return scanId;
	}

	public void setScanId(String scanId) {
		this.scanId = scanId;
	}

	public String getPushErp() {
		return pushErp;
	}

	public void setPushErp(String pushErp) {
		this.pushErp = pushErp;
	}
}
