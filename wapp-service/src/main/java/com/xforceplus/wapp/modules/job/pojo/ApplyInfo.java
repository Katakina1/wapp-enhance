package com.xforceplus.wapp.modules.job.pojo;

import java.util.List;

public class ApplyInfo extends BasePojo{
    private String batchNo;
	private String buyerTaxNo;
	
	private int  contentRows;
	
	private List<ApplyInvoice>invoices;

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getBuyerTaxNo() {
		return buyerTaxNo;
	}

	public void setBuyerTaxNo(String buyerTaxNo) {
		this.buyerTaxNo = buyerTaxNo;
	}

	public int getContentRows() {
		return contentRows;
	}

	public void setContentRows(int contentRows) {
		this.contentRows = contentRows;
	}

	public List<ApplyInvoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<ApplyInvoice> invoices) {
		this.invoices = invoices;
	}

	@Override
	public String toString() {
		return "ApplyInfo{" +
				"batchNo='" + batchNo + '\'' +
				", buyerTaxNo='" + buyerTaxNo + '\'' +
				", contentRows=" + contentRows +
				", invoices=" + invoices +
				'}';
	}
}
