package com.xforceplus.wapp.modules.xforceapi.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 红字信息表同步请求记录
 * 
 * @author just
 *
 */
@TableName("t_xf_rednotification_sync_req")
public class TXfRednotificationSyncReq implements Serializable {
	private static final long serialVersionUID = 1L;
	// 主键
	@TableId(type = IdType.AUTO)
	private Long id;
	// 请求流水号
	private String serialNo;
	// 集团代码
	private String tenantCode;
	// 终端唯一码
	private String terminalUn;
	// 设备唯一码
	private String deviceUn;
	// 红字信息表编号
	private String redNotificationNo;
	// 填开起始日期（红字信息表编号为空时，必填；格式：yyyyMMdd）
	private String startDate;// ":"20230221",
	// 填开截止日期（红字信息表编号为空时，必填；格式：yyyyMMdd）
	private String endDate;// ":"20230223",
	// 对应蓝票发票类型（s:纸专,se:电专;默认:s）
	private String originalInvoiceType;
	// 是否查询本地数据(只针对客户端税盘有效) false(默认): 查询局端最新数据 true: 查询客户端本地数据
	private String queryLocalData;
	// 发起结果
	private String reqResult;
	// 结果返回的流水号
	private String respSerialNo;
	// 返回结果code
	private String respResult;
	// 返回结果说明
	private String respResultMsg;
	// 创建时间
	private Date createdTime;
	// 最后修改时间
	private Date updatedTime;

	private Integer pageSize = 500;
	private Integer pageNo = 1;

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public TXfRednotificationSyncReq() {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getReqResult() {
		return reqResult;
	}

	public void setReqResult(String reqResult) {
		this.reqResult = reqResult;
	}

	public String getRespSerialNo() {
		return respSerialNo;
	}

	public void setRespSerialNo(String respSerialNo) {
		this.respSerialNo = respSerialNo;
	}

	public String getRespResult() {
		return respResult;
	}

	public void setRespResult(String respResult) {
		this.respResult = respResult;
	}

	public String getRespResultMsg() {
		return respResultMsg;
	}

	public void setRespResultMsg(String respResultMsg) {
		this.respResultMsg = respResultMsg;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

}
