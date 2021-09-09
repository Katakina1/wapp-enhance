package com.xforceplus.wapp.modules.signin.entity;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
* @ClassName: SignedInvoiceVo 
* @Description: TODO 
* @author yuanlz
* @date 2016年11月29日 下午7:11:01 
*  
*/

public class SignedInvoiceVo {

	private static final long serialVersionUID = 1L;

	/*
	 * field
	 */
	// id
	private Long id;
	
	// 扫描流水号
	private String invoiceSerialNo;
	// 扫描备注
	private String notes;
	// 扫描时间
	private String scandate;
	// 扫描状态
	private String invoiceStatus;
	// 发票代码
	private String invoiceCode;
	// 发票号码
	private String invoiceNo;
	// 发票类型
	private String invoiceType;
	// 购方税号
	private String gfTaxNo;
	//购方名称
	private String gfName;
	// 销方税号
	private String xfTaxNo;
	// 销方税号
	private String xfName;

	public String getXfName() {
		return xfName;
	}

	public void setXfName(String xfName) {
		this.xfName = xfName;
	}

	// 开票日期
	private String invoiceDate;
	// 金额
	private String invoiceAmount;
	// 税额
	private String taxAmount;
	// 价税合计
	private String totalAmount;
	// 扫描路径id
	private String scanPath;
	// 扫描路径id
	private Long scanPathId;
	// 操作员
	private String userName;
	// 操作人账户
	private String userAccount;
	// 操作
	private String operate;
	// uuid
	private String uuid;
	//签收时间
	private Date qsDate;
	// 是否需要核对底账
	private Integer  insideOutside;
	//修改前uuid
	private String scpz;
	//记录普票扫描成功数量
	private int successNum;
	//记录普票扫描失败数量
	private int failNum;
	//用于判断是否是修改操作
	private int type;
	//扫描唯一识别码\
	private String scanId;

	/**
	 * 校验码
	 */
	private  String checkCode;

	public Long getScanPathId() {
		return scanPathId;
	}
	public void setScanPathId(Long scanPathId) {
		this.scanPathId = scanPathId;
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getScanId() {
		return scanId;
	}
	public void setScanId(String scanId) {
		this.scanId = scanId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getSuccessNum() {
		return successNum;
	}
	public void setSuccessNum(int successNum) {
		this.successNum = successNum;
	}
	public int getFailNum() {
		return failNum;
	}
	public void setFailNum(int failNum) {
		this.failNum = failNum;
	}
	public String getGfName() {
		return gfName;
	}
	public void setGfName(String gfName) {
		this.gfName = gfName;
	}
	public String getScpz() {
		return scpz;
	}
	public void setScpz(String scpz) {
		this.scpz = scpz;
	}
	public Integer getInsideOutside() {
		return insideOutside;
	}
	public void setInsideOutside(Integer insideOutside) {
		this.insideOutside = insideOutside;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getQsDate() {
		return qsDate;
	}
	public void setQsDate(Date qsDate) {
		this.qsDate = qsDate;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getInvoiceSerialNo() {
		return invoiceSerialNo;
	}
	public void setInvoiceSerialNo(String invoiceSerialNo) {
		this.invoiceSerialNo = invoiceSerialNo;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getScandate() {
		return scandate;
	}
	public void setScandate(String scandate) {
		this.scandate = scandate;
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
	public String getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(String invoiceType) {
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
	public String getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public String getInvoiceAmount() {
		return invoiceAmount;
	}
	public void setInvoiceAmount(String invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}
	public String getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getScanPath() {
		return scanPath;
	}
	public void setScanPath(String scanPath) {
		this.scanPath = scanPath;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getOperate() {
		return operate;
	}
	public void setOperate(String operate) {
		this.operate = operate;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getCheckCode() {
		return checkCode;
	}
	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}
}
