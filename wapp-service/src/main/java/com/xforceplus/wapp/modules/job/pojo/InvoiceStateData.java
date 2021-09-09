package com.xforceplus.wapp.modules.job.pojo;

/**
 * 
 * @Title Data.java
 * @Description 发票状态获取请求数据项
 * @author th FU
 * @date 2018年04月17日 上午07:00:02
 */
public class InvoiceStateData extends BasePojo{

	private static final long serialVersionUID = 5186848186806062582L;
	//批次号
	private String batchNo;
	//购方税号
	private String buyerTaxNo;
	//状态变更日期时间 开始
	private String statusChangeDateBegin;
	//状态变更日期时间 结束
	private String statusChangeDateEnd;
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

	public String getStatusChangeDateBegin() {
		return statusChangeDateBegin;
	}

	public void setStatusChangeDateBegin(String statusChangeDateBegin) {
		this.statusChangeDateBegin = statusChangeDateBegin;
	}

	public String getStatusChangeDateEnd() {
		return statusChangeDateEnd;
	}

	public void setStatusChangeDateEnd(String statusChangeDateEnd) {
		this.statusChangeDateEnd = statusChangeDateEnd;
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
}
