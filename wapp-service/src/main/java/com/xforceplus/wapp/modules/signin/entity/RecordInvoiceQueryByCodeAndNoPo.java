package com.xforceplus.wapp.modules.signin.entity;

import java.util.Date;

/**
 * 通过发票code和no查询底账表
 * @author yuminghui3
 *
 */
public class RecordInvoiceQueryByCodeAndNoPo {
	
	private String invoiceCode;
	private String invoiceNo;
	private Date date;
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
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "RecordInvoiceQueryByCodeAndNoPo [invoiceCode=" + invoiceCode
				+ ", invoiceNo=" + invoiceNo + "]";
	}
}
