package com.xforceplus.wapp.modules.job.pojo;

import java.util.List;

public class ApplyInvoice extends BasePojo{

	private String invoiceCode;
	
	private String invoiceNo;
	
	private String buyerTaxNo;
	
	private String applyTaxPeriod;
	
	private String applyRzlx;

	private String invoiceType;

	private String yxse;

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

	public String getBuyerTaxNo() {
		return buyerTaxNo;
	}

	public void setBuyerTaxNo(String buyerTaxNo) {
		this.buyerTaxNo = buyerTaxNo;
	}

	public String getApplyTaxPeriod() {
		return applyTaxPeriod;
	}

	public void setApplyTaxPeriod(String applyTaxPeriod) {
		this.applyTaxPeriod = applyTaxPeriod;
	}

	public String getApplyRzlx() {
		return applyRzlx;
	}

	public void setApplyRzlx(String applyRzlx) {
		this.applyRzlx = applyRzlx;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getYxse() {
		return yxse;
	}

	public void setYxse(String yxse) {
		this.yxse = yxse;
	}
}
