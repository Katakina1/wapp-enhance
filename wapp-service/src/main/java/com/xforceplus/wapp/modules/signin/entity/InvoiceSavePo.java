package com.xforceplus.wapp.modules.signin.entity;

import java.math.BigDecimal;
import java.util.Date;




public class InvoiceSavePo  {

	private static final long serialVersionUID = 1L;
	
	/*
	 * field
	 */
	//  ==============  可直接获取的属性
	// 扫描备注
	private String notes;
	// 发票代码
	private String invoiceCode;
	// 发票号码
	private String invoiceNo;
	// 购方税号
	private String gfTaxNo;
	//购方名称
	private String gfName;
	// 金额
	private BigDecimal invoiceAmount;
	// 税额
	private BigDecimal taxAmount;
	// 价税合计
	private BigDecimal totalAmount;
	// 销方税号
	private String xfTaxNo;
	// 销方税号
	private String xfName;
	// 开票日期
	private Date invoiceDate;
	
	//  ==============  由参数传递的属性
	// 扫描路径id
	private Long scanPathId;
	
	//  ==============  值固定的属性
	// 有效状态
	private String valid;

	//  ==============  间接获取的属性
	// 扫描流水号
	private String invoiceSerialNo;
	// 发票类型
	private String invoiceType;
	// 签收状态
	private String invoiceStatus;
	// 唯一索引
	private String uuid;
	// 用户账号
	private String userAccount;
	// 用户姓名
	private String userName;
	// 创建时间
	private String createDate;
	// id
	private Long id;
	//签收时间
	private Date qsDate;
	
	// 是否需要核对底账
	private Integer  insideOutside;
	// 扫描唯一识别码 
	private String  scanId;

	public String getXfName() {
		return xfName;
	}

	public void setXfName(String xfName) {
		this.xfName = xfName;
	}

	public String getScanId() {
		return scanId;
	}
	public void setScanId(String scanId) {
		this.scanId = scanId;
	}
	public String getGfName() {
		return gfName;
	}
	public void setGfName(String gfName) {
		this.gfName = gfName;
	}
	public Integer getInsideOutside() {
		return insideOutside;
	}
	public void setInsideOutside(Integer insideOutside) {
		this.insideOutside = insideOutside;
	}
	public Date getQsDate() {
		return qsDate;
	}
	public void setQsDate(Date qsDate) {
		this.qsDate = qsDate;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	/*
	 * setter / getter
	 */
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
	public String getGfTaxNo() {
		return gfTaxNo;
	}
	public void setGfTaxNo(String gfTaxNo) {
		this.gfTaxNo = gfTaxNo;
	}
	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}
	public void setInvoiceAmount(BigDecimal invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}
	public BigDecimal getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
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
	public Long getScanPathId() {
		return scanPathId;
	}
	public void setScanPathId(Long scanPathId) {
		this.scanPathId = scanPathId;
	}
	public String getValid() {
		return valid;
	}
	public void setValid(String valid) {
		this.valid = valid;
	}
	public String getInvoiceSerialNo() {
		return invoiceSerialNo;
	}
	public void setInvoiceSerialNo(String invoiceSerialNo) {
		this.invoiceSerialNo = invoiceSerialNo;
	}
	public String getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getInvoiceStatus() {
		return invoiceStatus;
	}

	public void setInvoiceStatus(String invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
