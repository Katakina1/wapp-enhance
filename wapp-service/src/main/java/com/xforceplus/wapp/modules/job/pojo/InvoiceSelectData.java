package com.xforceplus.wapp.modules.job.pojo;

/**
 * 
 * @Title Data.java
 * @Description 发票状态获取请求数据项
 * @author th FU
 * @date 2018年04月17日 上午07:00:02
 */
public class InvoiceSelectData extends BasePojo{

	private static final long serialVersionUID = 5186848186806062582L;
	//批次号
	private String batchNo;
	//购方税号
	private String buyerTaxNo;
	//状态变更日期时间 开始
	private String acquisitionDateBegin;
	//状态变更日期时间 结束
	private String acquisitionDateEnd;
	//开始行数
	private Integer startRow;
	//状态标志 1时继续请求，0时结束请求
	private String status;


	public static long getSerialVersionUID() {
		return serialVersionUID;
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


	public Integer getStartRow() {
		return startRow;
	}

	public void setStartRow(Integer startRow) {
		this.startRow = startRow;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
}
