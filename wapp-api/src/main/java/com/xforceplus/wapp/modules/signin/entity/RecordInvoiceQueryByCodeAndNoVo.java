package com.xforceplus.wapp.modules.signin.entity;

import java.util.Date;
/**
 *根据code和no查询底账表返回数据
 * @author yuminghui3
 *
 */
public class RecordInvoiceQueryByCodeAndNoVo {
	private String invoiceCode;
	private String invoiceNo;
	private Integer invoiceType;
	private String gfTaxNo;
	private String gfName;
	private String xfTaxNo;

	public String getXfName() {
		return xfName;
	}

	public void setXfName(String xfName) {
		this.xfName = xfName;
	}

	private String xfName;
	private Date invoiceDate;
	private Double invoiceAmount;
	private Double taxAmount;
	private Double totalAmount;
	private Integer invoiceStatus;
	
	public String getGfName() {
		return gfName;
	}
	public void setGfName(String gfName) {
		this.gfName = gfName;
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
	public Integer getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(Integer invoiceType) {
		this.invoiceType = invoiceType;
	}
	public String getGfTaxNo() {
		return gfTaxNo;
	}
	public void setGfTaxNo(String gfTaxNo) {
		this.gfTaxNo = gfTaxNo;
	}
	public String getXfTaxNo() {
		return xfTaxNo;
	}
	public void setXfTaxNo(String xfTaxNo) {
		this.xfTaxNo = xfTaxNo;
	}
	public Date getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public Double getInvoiceAmount() {
		return invoiceAmount;
	}
	public void setInvoiceAmount(Double invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}
	public Double getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(Double taxAmount) {
		this.taxAmount = taxAmount;
	}
	public Double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	public Integer getInvoiceStatus() {
		return invoiceStatus;
	}
	public void setInvoiceStatus(Integer invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}
	@Override
	public String toString() {
		return "RecordInvoiceQueryByCodeAndNoVo [invoiceCode=" + invoiceCode
				+ ", invoiceNo=" + invoiceNo + ", invoiceType=" + invoiceType
				+ ", gfTaxNo=" + gfTaxNo + ", xfTaxNo=" + xfTaxNo
				+ ", invoiceDate=" + invoiceDate + ", invoiceAmount="
				+ invoiceAmount + ", taxAmount=" + taxAmount + ", totalAmount="
				+ totalAmount + ", invoiceStatus=" + invoiceStatus + "]";
	}
	
}
