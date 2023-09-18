package com.xforceplus.wapp.modules.xforceapi.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_xf_rednotification_sync")
public class TXfRednotificationSync implements Serializable {
	private static final long serialVersionUID = -1225375503383618804L;
	// 主键
	@TableId(type = IdType.AUTO)
	private Long id;
	// 请求流水号
	private String serialNo;
	//业务单号，从t_xf_red_notification获取
	private String billNo;
	// 非必须 原发票代码
	private String originalInvoiceCode;// ": "",
	// 非必须 原发票号码
	private String originalInvoiceNo;// ": "",
	// 非必须 购方税号
	private String purchaserTaxCode;// ": "",
	// 非必须 购方名称
	private String purchaserName;// ": "",
	// 非必须 红字信息编号
	private String redNotificationNo;// ": "",
	// 非必须 销方税号
	private String sellerTaxCode;// ": "",
	// 非必须 销方名称
	private String sellerName;// ": "",
	// 非必须 状态
	private String statusCode;// ": "",
	// 非必须 状态描述
	private String statusMsg;// ": "",
	// 金额信息
	// 价税合计（保留小数点后2位）
	private BigDecimal amountWithTax;// ": -112991.45,
	// 不含税金额（保留小数点后2位）
	private BigDecimal amountWithoutTax;// ": -99992.44,
	// 税额（保留小数点后2位）
	private BigDecimal taxAmount;// ": -12999.01
	// 非必须填开日期，格式：yyyyMMdd
	private String applyDate;// ": "20230222",
	// 税号校验结果
	private String taxNoResult;// varchar(16) NOT NULL,
	// 税号校验说明
	private String taxNoResultMsg;// varchar(2000) NOT NULL,
	// 金额校验结果
	private String amountResult;// ] varchar(16) NOT NULL,
	// 金额校验说明
	private String amountResultMsg;// ] varchar(2000) NOT NULL,
	// 创建时间
	private Date createdTime;
	// 最后修改时间
	private Date updatedTime;

	public TXfRednotificationSync() {
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

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getOriginalInvoiceCode() {
		return originalInvoiceCode;
	}

	public void setOriginalInvoiceCode(String originalInvoiceCode) {
		this.originalInvoiceCode = originalInvoiceCode;
	}

	public String getOriginalInvoiceNo() {
		return originalInvoiceNo;
	}

	public void setOriginalInvoiceNo(String originalInvoiceNo) {
		this.originalInvoiceNo = originalInvoiceNo;
	}

	public String getPurchaserTaxCode() {
		return purchaserTaxCode;
	}

	public void setPurchaserTaxCode(String purchaserTaxCode) {
		this.purchaserTaxCode = purchaserTaxCode;
	}

	public String getPurchaserName() {
		return purchaserName;
	}

	public void setPurchaserName(String purchaserName) {
		this.purchaserName = purchaserName;
	}

	public String getRedNotificationNo() {
		return redNotificationNo;
	}

	public void setRedNotificationNo(String redNotificationNo) {
		this.redNotificationNo = redNotificationNo;
	}

	public String getSellerTaxCode() {
		return sellerTaxCode;
	}

	public void setSellerTaxCode(String sellerTaxCode) {
		this.sellerTaxCode = sellerTaxCode;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}

	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}

	public String getTaxNoResult() {
		return taxNoResult;
	}

	public void setTaxNoResult(String taxNoResult) {
		this.taxNoResult = taxNoResult;
	}

	public String getTaxNoResultMsg() {
		return taxNoResultMsg;
	}

	public void setTaxNoResultMsg(String taxNoResultMsg) {
		this.taxNoResultMsg = taxNoResultMsg;
	}

	public String getAmountResult() {
		return amountResult;
	}

	public void setAmountResult(String amountResult) {
		this.amountResult = amountResult;
	}

	public String getAmountResultMsg() {
		return amountResultMsg;
	}

	public void setAmountResultMsg(String amountResultMsg) {
		this.amountResultMsg = amountResultMsg;
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
