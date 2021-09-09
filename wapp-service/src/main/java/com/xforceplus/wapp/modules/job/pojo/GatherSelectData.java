package com.xforceplus.wapp.modules.job.pojo;

import java.util.List;

public class GatherSelectData extends BasePojo{

	private static final long serialVersionUID = 2651534362649780737L;
	
	private Integer totalRows;
	
	private Integer  startRow;
	
	private Integer contentRows;
	
	private List<?> invoices;

	public Integer getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(Integer totalRows) {
		this.totalRows = totalRows;
	}

	public Integer getStartRow() {
		return startRow;
	}

	public void setStartRow(Integer startRow) {
		this.startRow = startRow;
	}

	public Integer getContentRows() {
		return contentRows;
	}

	public void setContentRows(Integer contentRows) {
		this.contentRows = contentRows;
	}


	public List<?> getInvoices() {
		return invoices;
	}


	public void setInvoices(List<?> invoices) {
		this.invoices = invoices;
	}

	@Override
	public String toString() {
		return "GatherSelectData [totalRows=" + totalRows + ", startRow="
				+ startRow + ", contentRows=" + contentRows + ", invoices="
				+ invoices + "]";
	}


}
