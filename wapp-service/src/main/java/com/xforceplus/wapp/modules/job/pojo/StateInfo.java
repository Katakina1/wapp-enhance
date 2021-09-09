package com.xforceplus.wapp.modules.job.pojo;

public class StateInfo extends BasePojo{

	private static final long serialVersionUID = -5143641802293117517L;
	//发票代码
	private String invoiceCode;
	//发票号码
	private String invoiceNo;
	//发票状态
	private String invoiceStatus;
	//认证状态
	private String legalizeState;
	//认证时间
	private String legalizeDate;
	//认证类型
//	private String rzlx;
	private String legalizeBelongDate;

	private String legalizeType;

	public String getLegalizeType() {
		return legalizeType;
	}

	public void setLegalizeType(String legalizeType) {
		this.legalizeType = legalizeType;
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

	
	public String getLegalizeBelongDate() {
		return legalizeBelongDate;
	}

	public void setLegalizeBelongDate(String legalizeBelongDate) {
		this.legalizeBelongDate = legalizeBelongDate;
	}

	@Override
	public String toString() {
		return "StateInfo [invoiceCode=" + invoiceCode + ", invoiceNo="
				+ invoiceNo + ", invoiceStatus=" + invoiceStatus
				+ ", legalizeState=" + legalizeState + ", legalizeDate="
				+ legalizeDate + ", legalizeBelongDate=" + legalizeBelongDate
				+ "]";
	}

	
//	public String getRzlx() {
//		return rzlx;
//	}
//
//	public void setRzlx(String rzlx) {
//		this.rzlx = rzlx;
//	}




}
