package com.xforceplus.wapp.modules.job.pojo;

import java.util.List;

public class StatusInfo extends BasePojo{

	private static final long serialVersionUID = -3296913314304312738L;

	private Integer totalRows;
	
	private Integer  startRow;
	
	private Integer contentRows;
	
	private List<StateInfo> invoices;

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

	public List<StateInfo> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<StateInfo> invoices) {
		this.invoices = invoices;
	}

	@Override
	public String toString() {
		return "StatusInfo [totalRows=" + totalRows + ", startRow=" + startRow + ", contentRows=" + contentRows
				+ ", invoices=" + invoices + "]";
	}
}
