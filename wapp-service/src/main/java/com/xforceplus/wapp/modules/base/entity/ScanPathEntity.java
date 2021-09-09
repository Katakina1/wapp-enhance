package com.xforceplus.wapp.modules.base.entity;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 扫描点
 *
 * Created by Daily.zhang on 2018/04/13.
 */

public class ScanPathEntity extends BaseEntity implements Serializable {
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

	private Long[] ids;
    /**
	 * 扫描配置id
	 */
	private Long id;
	
	private Long valid;
	
	/**
	 * 扫描配置路径
	 */
	private String scanPath;
	
	/**
	 * 创建时间 
	 */
	private Date createDate;
	
	/**
	 * 更改时间
	 */
	private Date updateDate;
	
	private String invoiceRemark;
	

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getScanPath() {
		return scanPath;
	}

	public void setScanPath(String scanPath) {
		this.scanPath = scanPath;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
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

	public Long[] getIds() {
		return ids;
	}

	public void setIds(Long[] ids) {
		this.ids = ids;
	}

	public Long getValid() {
		return valid;
	}

	public void setValid(Long valid) {
		this.valid = valid;
	}

	public String getPushErp() {
		return pushErp;
	}

	public void setPushErp(String pushErp) {
		this.pushErp = pushErp;
	}


}
