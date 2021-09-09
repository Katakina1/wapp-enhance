package com.xforceplus.wapp.modules.signin.entity;

import java.math.BigDecimal;
import java.util.Date;



/** 
* @ClassName: RecordInvoiceCreateSignPo 
* @Description: 无底账发票签收时创建底账入库参数po
* @author yuanlz
* @date 2016年12月2日 下午3:09:05 
*  
*/
public class RecordInvoiceCreateSignPo{

	private static final long serialVersionUID = 1L;
	
	//扫描备注
	private String notes;
    // 发票类型
    private String invoiceType;
    // 发票代码
    private String invoiceCode;
    // 发票号码
    private String invoiceNo;
    // 开票日期
    private Date invoiceDate;
    // 购方税号
    private String gfTaxNo;
    //购方税号
    private String gfName;
    // 销方税号
    private String xfTaxNo;
    // 金额
    private BigDecimal invoiceAmount;
    // 税额
    private BigDecimal taxAmount;
    // 价格合计
    private BigDecimal totalAmount;
    // uuid
    private String uuid;
    
    private Long scanPahtId;
    //签收时间
    private Date qsDate;
    //修改前uuid
    private String scpz;
    //创建时间
    private String createDate;
    //扫描路径名称
    private String scanPath;


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
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

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getGfTaxNo() {
		return gfTaxNo;
	}

	public void setGfTaxNo(String gfTaxNo) {
		this.gfTaxNo = gfTaxNo;
	}

	public String getGfName() {
		return gfName;
	}

	public void setGfName(String gfName) {
		this.gfName = gfName;
	}

	public String getXfTaxNo() {
		return xfTaxNo;
	}

	public void setXfTaxNo(String xfTaxNo) {
		this.xfTaxNo = xfTaxNo;
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Long getScanPahtId() {
		return scanPahtId;
	}

	public void setScanPahtId(Long scanPahtId) {
		this.scanPahtId = scanPahtId;
	}

	public Date getQsDate() {
		return qsDate;
	}

	public void setQsDate(Date qsDate) {
		this.qsDate = qsDate;
	}

	public String getScpz() {
		return scpz;
	}

	public void setScpz(String scpz) {
		this.scpz = scpz;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getScanPath() {
		return scanPath;
	}

	public void setScanPath(String scanPath) {
		this.scanPath = scanPath;
	}
}