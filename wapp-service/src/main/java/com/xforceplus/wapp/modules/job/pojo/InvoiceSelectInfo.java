package com.xforceplus.wapp.modules.job.pojo;


import java.util.Date;

public class InvoiceSelectInfo extends BasePojo{

	private static final long serialVersionUID = 9140037500021701297L;

	private String invoiceCode;
	
	private String invoiceNo;
	
	//新加字段
	private String currentTaxPeriod;
	
	private String legalizeEndDate;
	
	private String legalizeInvoiceDateBegin;
	
	private String legalizeInvoiceDateEnd;

	//新加字段

	private String invoiceType;
	
	private String  invoiceDate;
	
	private String buyerName;
	
	private String buyerTaxNo;
	
	private String salerName;
	
	private String salerTaxNo;
	
	private String invoiceAmount;
	
	private String taxAmount;
	
//	private String totalAmount;
	
//	private String remark;
	
	private String invoiceStatus;
	
	private String legalizeState;
	
	private String legalizeDate;
	private String legalizeBlongDate;
	//认证方式
	private String legalizeType;
	private String sfdbts;
	private String rzlx;
	private String checkCode;

	private String uuid;

	private Date qsDate;
	private String qsStatus;
	private String qsType;

	private String authStatus;

	public String getLegalizeType() {
		return legalizeType;
	}

	public void setLegalizeType(String legalizeType) {
		this.legalizeType = legalizeType;
	}

	public String getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}

	public Date getQsDate() {
		return qsDate;
	}

	public void setQsDate(Date qsDate) {
		this.qsDate = qsDate;
	}

	public String getQsStatus() {
		return qsStatus;
	}

	public void setQsStatus(String qsStatus) {
		this.qsStatus = qsStatus;
	}

	public String getQsType() {
		return qsType;
	}

	public void setQsType(String qsType) {
		this.qsType = qsType;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getInvoiceCode() {
		return invoiceCode;
	}
	
	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

//	public String getBuyerName() {
//		return buyerName;
//	}
//
//	public void setBuyerName(String buyerName) {
//		this.buyerName = buyerName;
//	}

	public String getBuyerTaxNo() {
		return buyerTaxNo;
	}

	public void setBuyerTaxNo(String buyerTaxNo) {
		this.buyerTaxNo = buyerTaxNo;
	}


	public String getSalerName() {
		return salerName;
	}

	public void setSalerName(String salerName) {
		this.salerName = salerName;
	}


	public String getSalerTaxNo() {
		return salerTaxNo;
	}

	public void setSalerTaxNo(String salerTaxNo) {
		this.salerTaxNo = salerTaxNo;
	}

	public String getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(String invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public String getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
	}

//	public String getTotalAmount() {
//		return totalAmount;
//	}
//
//	public void setTotalAmount(String totalAmount) {
//		this.totalAmount = totalAmount;
//	}

//	public String getRemark() {
//		return remark;
//	}
//
//	public void setRemark(String remark) {
//		this.remark = remark;
//	}

	public String getInvoiceStatus() {
		return invoiceStatus;
	}

	public void setInvoiceStatus(String invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	public String getLegalizeState() {
		return legalizeState;
	}

	public void setLegalizeState(String legalizeState) {
		this.legalizeState = legalizeState;
	}

	public String getLegalizeDate() {
		return legalizeDate;
	}

	public void setLegalizeDate(String legalizeDate) {
		this.legalizeDate = legalizeDate;
	}
	
	public String getCurrentTaxPeriod() {
		return currentTaxPeriod;
	}

	public void setCurrentTaxPeriod(String currentTaxPeriod) {
		this.currentTaxPeriod = currentTaxPeriod;
	}

	public String getLegalizeEndDate() {
		return legalizeEndDate;
	}

	public void setLegalizeEndDate(String legalizeEndDate) {
		this.legalizeEndDate = legalizeEndDate;
	}

	public String getLegalizeInvoiceDateBegin() {
		return legalizeInvoiceDateBegin;
	}

	public void setLegalizeInvoiceDateBegin(String legalizeInvoiceDateBegin) {
		this.legalizeInvoiceDateBegin = legalizeInvoiceDateBegin;
	}

	public String getLegalizeInvoiceDateEnd() {
		return legalizeInvoiceDateEnd;
	}

	public void setLegalizeInvoiceDateEnd(String legalizeInvoiceDateEnd) {
		this.legalizeInvoiceDateEnd = legalizeInvoiceDateEnd;
	}

	public String getLegalizeBlongDate() {
		return legalizeBlongDate;
	}

	public void setLegalizeBlongDate(String legalizeBlongDate) {
		this.legalizeBlongDate = legalizeBlongDate;
	}

	
	public String getSfdbts() {
		return sfdbts;
	}

	public void setSfdbts(String sfdbts) {
		this.sfdbts = sfdbts;
	}

	public String getRzlx() {
		return rzlx;
	}

	public void setRzlx(String rzlx) {
		this.rzlx = rzlx;
	}

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	@Override
	public String toString() {
		return "InvoiceSelectInfo [invoiceCode=" + invoiceCode + ", invoiceNo="
				+ invoiceNo + ", currentTaxPeriod=" + currentTaxPeriod
				+ ", legalizeEndDate=" + legalizeEndDate
				+ ", legalizeInvoiceDateBegin=" + legalizeInvoiceDateBegin
				+ ", legalizeInvoiceDateEnd=" + legalizeInvoiceDateEnd
				+ ", invoiceType=" + invoiceType + ", invoiceDate="
				+ invoiceDate + ", buyerTaxNo=" + buyerTaxNo + ", salerName="
				+ salerName + ", salerTaxNo=" + salerTaxNo + ", invoiceAmount="
				+ invoiceAmount + ", taxAmount=" + taxAmount
				+ ", invoiceStatus=" + invoiceStatus + ", legalizeState="
				+ legalizeState + ", legalizeDate=" + legalizeDate
				+ ", legalizeBlongDate=" + legalizeBlongDate + ", sfdbts="
				+ sfdbts + ", rzlx=" + rzlx + ", checkCode=" + checkCode + "]";
	}

}
