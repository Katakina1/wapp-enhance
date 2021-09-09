package com.xforceplus.wapp.modules.job.pojo;

import java.util.Date;

/**
 * 
 * @Title TaxCurrent.java
 * @Description 企业税款所属期
 * @author X Yang
 * @date  
 */
public class LegalizeResult extends BasePojo{

	private String invoiceCode;
	private String invoiceNo;
	private String legalizeState;
	private String legalizeDate;
	private String authState;
	private String failureReason;
	private String legalizeType;
	private String legalizeBelongDate;
	private String rzlx;

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

	public String getAuthState() {
		return authState;
	}

	public void setAuthState(String authState) {
		this.authState = authState;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public String getLegalizeType() {
		return legalizeType;
	}

	public void setLegalizeType(String legalizeType) {
		this.legalizeType = legalizeType;
	}

	public String getLegalizeBelongDate() {
		return legalizeBelongDate;
	}

	public void setLegalizeBelongDate(String legalizeBelongDate) {
		this.legalizeBelongDate = legalizeBelongDate;
	}

	public String getRzlx() {
		return rzlx;
	}

	public void setRzlx(String rzlx) {
		this.rzlx = rzlx;
	}
}
