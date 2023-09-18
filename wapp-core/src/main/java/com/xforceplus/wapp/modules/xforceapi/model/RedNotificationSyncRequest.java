package com.xforceplus.wapp.modules.xforceapi.model;

import java.io.Serializable;

/**
 * 红字信息表下载请求参数
 * 
 * @author just
 *
 */
public class RedNotificationSyncRequest implements Serializable {

	private static final long serialVersionUID = -8641752188287568292L;
	// 流水号（20个字符）
	private String serialNo;// ":"5F14A530921C0F0B6DA412CB2E03D3A9-0221",
	//租户代码
	private String tenantCode;
	// 终端唯一码（与设备唯一码不能同时为空）
	private String terminalUn;// ":"",
	// 设备唯一码 （单盘下载时必传，与终端唯一码不能同时为空）
	private String deviceUn;// ":"8TKSQZTM",
	// 红字信息表编号（填开日期都为空时，必填）
	private String redNotificationNo;// ":"",
	// 填开起始日期（红字信息表编号为空时，必填；格式：yyyyMMdd）
	private String startDate;// ":"20230221",
	// 填开截止日期（红字信息表编号为空时，必填；格式：yyyyMMdd）
	private String endDate;// ":"20230223",
	// 对应蓝票发票类型（s:纸专,se:电专;默认:s）
	private String originalInvoiceType;// ":"",
	// 是否查询本地数据(只针对客户端税盘有效) false(默认): 查询局端最新数据 true: 查询客户端本地数据
	private String queryLocalData;// ":""

	public RedNotificationSyncRequest() {

	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public String getTerminalUn() {
		return terminalUn;
	}

	public void setTerminalUn(String terminalUn) {
		this.terminalUn = terminalUn;
	}

	public String getDeviceUn() {
		return deviceUn;
	}

	public void setDeviceUn(String deviceUn) {
		this.deviceUn = deviceUn;
	}

	public String getRedNotificationNo() {
		return redNotificationNo;
	}

	public void setRedNotificationNo(String redNotificationNo) {
		this.redNotificationNo = redNotificationNo;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getOriginalInvoiceType() {
		return originalInvoiceType;
	}

	public void setOriginalInvoiceType(String originalInvoiceType) {
		this.originalInvoiceType = originalInvoiceType;
	}

	public String getQueryLocalData() {
		return queryLocalData;
	}

	public void setQueryLocalData(String queryLocalData) {
		this.queryLocalData = queryLocalData;
	}

}
