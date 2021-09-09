package com.xforceplus.wapp.modules.job.pojo;

import java.util.Date;

public class State extends BasePojo{

	private static final long serialVersionUID = -7944750189078740895L;

	private String invoiceCode;
	
	private String invoiceNo;
	
	private String invoiceStatus;
	
	private String legalizeState;
	
	private Date legalizeDate;
	private String legalizeBelongDate;

    private String authStatus;



	private String legalizeType;
//	private Integer legalizeLx;

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



	public Date getLegalizeDate() {
		return legalizeDate;
	}

	public void setLegalizeDate(Date legalizeDate) {
		this.legalizeDate = legalizeDate;
	}


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

	public String getLegalizeBelongDate() {
		return legalizeBelongDate;
	}

	public void setLegalizeBelongDate(String legalizeBelongDate) {
		this.legalizeBelongDate = legalizeBelongDate;
	}

	public String getLegalizeType() {
		return legalizeType;
	}

	public void setLegalizeType(String legalizeType) {
		this.legalizeType = legalizeType;
	}
	@Override
	public String toString() {
		return "State [invoiceCode=" + invoiceCode + ", invoiceNo=" + invoiceNo
				+ ", invoiceStatus=" + invoiceStatus + ", legalizeState="
				+ legalizeState + ", legalizeDate=" + legalizeDate
				+ ", legalizeBelongDate=" + legalizeBelongDate + "]";
	}

	public String getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}

//	public Integer getLegalizeLx() {
//		return legalizeLx;
//	}
//
//	public void setLegalizeLx(Integer legalizeLx) {
//		this.legalizeLx = legalizeLx;
//	}



}
