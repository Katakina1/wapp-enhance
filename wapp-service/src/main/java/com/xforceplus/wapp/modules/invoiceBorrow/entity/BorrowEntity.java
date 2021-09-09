package com.xforceplus.wapp.modules.invoiceBorrow.entity;


public class BorrowEntity {
	//ID
	private Long id;
	//借阅人
	private String borrowUser;
	//借阅原因
	private String borrowReason;
	//借阅时间
	private String borrowDate;
	//操作类型(0-借阅,1-归还)
	private String operateType;
	//发票代码
	private String invoiceCode;
	//发票号码
	private String invoiceNo;
	//供应商号
	private String venderId;
	//装订册号
	private  String bbindingno;
	//装箱号
	private  String packingno;
	//借阅部门
	private  String borrowDept;
	//凭证号
	private String certificateNo;
	//jvcode
	private String jvCode;
	//companyCode
	private String companyCode;

	private String epsNo;

	private String uuid;
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}


	public String getJvCode() {
		return jvCode;
	}

	public void setJvCode(String jvCode) {
		this.jvCode = jvCode;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getCertificateNo() {
		return certificateNo;
	}

	public void setCertificateNo(String certificateNo) {
		this.certificateNo = certificateNo;
	}

	public String getBorrowDept() {
		return borrowDept;
	}

	public void setBorrowDept(String borrowDept) {
		this.borrowDept = borrowDept;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVenderId() {
		return venderId;
	}

	public void setVenderId(String venderId) {
		this.venderId = venderId;
	}

	public String getBbindingno() {
		return bbindingno;
	}

	public void setBbindingno(String bbindingno) {
		this.bbindingno = bbindingno;
	}

	public String getPackingno() {
		return packingno;
	}

	public void setPackingno(String packingno) {
		this.packingno = packingno;
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

	private String[] ids;

	public String[] getIds() {
		return ids;
	}

	public void setIds(String[] ids) {
		this.ids = ids;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public String getBorrowUser() {
		return borrowUser;
	}

	public void setBorrowUser(String borrowUser) {
		this.borrowUser = borrowUser;
	}

	public String getBorrowReason() {
		return borrowReason;
	}

	public void setBorrowReason(String borrowReason) {
		this.borrowReason = borrowReason;
	}

	public String getBorrowDate() {
		return borrowDate;
	}

	public void setBorrowDate(String borrowDate) {
		this.borrowDate = borrowDate;
	}

	public String getEpsNo() {
		return epsNo;
	}

	public void setEpsNo(String epsNo) {
		this.epsNo = epsNo;
	}
}
