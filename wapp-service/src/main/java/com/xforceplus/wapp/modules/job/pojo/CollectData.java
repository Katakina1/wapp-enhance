package com.xforceplus.wapp.modules.job.pojo;

/**
 * 
 * @Title Data.java
 * @Description 采集获取请求数据项
 * @author X Yang
 * @date 2017年6月7日 上午10:00:02
 */
public class CollectData extends BasePojo{

	private static final long serialVersionUID = 5186848186806062582L;
	//批次号
	private String batchNo;
	//购方税号
	private String buyerTaxNo;
	//开始时间
	private String acquisitionDateBegin;
	//结束时间
	private String acquisitionDateEnd;
	//开始行数
	private Integer startRow;
	//状态标志 1时继续请求，0时结束请求
	private String status;
	//发票类型
	private String invoiceType;
	
	
	
	
	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

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

	public String getAcquisitionDateBegin() {
		return acquisitionDateBegin;
	}

	public void setAcquisitionDateBegin(String acquisitionDateBegin) {
		this.acquisitionDateBegin = acquisitionDateBegin;
	}

	public String getAcquisitionDateEnd() {
		return acquisitionDateEnd;
	}

	public void setAcquisitionDateEnd(String acquisitionDateEnd) {
		this.acquisitionDateEnd = acquisitionDateEnd;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatus() {
		return status;
	}
	public Integer getStartRow() {
		return startRow;
	}

	public void setStartRow(Integer startRow) {
		this.startRow = startRow;
	}

	
}
