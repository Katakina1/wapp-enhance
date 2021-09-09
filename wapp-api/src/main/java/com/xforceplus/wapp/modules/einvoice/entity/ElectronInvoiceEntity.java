package com.xforceplus.wapp.modules.einvoice.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created on 2018/04/12.
 * @author marvin
 * 电票上传实体类
 */
@Getter
@Setter
@ToString
public class ElectronInvoiceEntity extends AbstractBaseDomain {

    private static final long serialVersionUID = -8006603698970334917L;
    
    private Long id;

    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
     * 发票类型
     */
    private String invoiceType;
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

	public String getInvoiceSerialNo() {
		return invoiceSerialNo;
	}

	public void setInvoiceSerialNo(String invoiceSerialNo) {
		this.invoiceSerialNo = invoiceSerialNo;
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

	public String getXfName() {
		return xfName;
	}

	public void setXfName(String xfName) {
		this.xfName = xfName;
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

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
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

	public String getQsType() {
		return qsType;
	}

	public void setQsType(String qsType) {
		this.qsType = qsType;
	}

	public String getQsStatus() {
		return qsStatus;
	}

	public void setQsStatus(String qsStatus) {
		this.qsStatus = qsStatus;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getQsDate() {
		return qsDate;
	}

	public void setQsDate(Date qsDate) {
		this.qsDate = qsDate;
	}

	public String getScanId() {
		return scanId;
	}

	public void setScanId(String scanId) {
		this.scanId = scanId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public String getPdfName() {
		return pdfName;
	}

	public void setPdfName(String pdfName) {
		this.pdfName = pdfName;
	}

	public Boolean getReadPdfSuccess() {
		return readPdfSuccess;
	}

	public void setReadPdfSuccess(Boolean readPdfSuccess) {
		this.readPdfSuccess = readPdfSuccess;
	}

	public Boolean getSaveRepeat() {
		return saveRepeat;
	}

	public void setSaveRepeat(Boolean saveRepeat) {
		this.saveRepeat = saveRepeat;
	}

	public Boolean getCheckSuccess() {
		return checkSuccess;
	}

	public void setCheckSuccess(Boolean checkSuccess) {
		this.checkSuccess = checkSuccess;
	}

	public String getResultTip() {
		return resultTip;
	}

	public void setResultTip(String resultTip) {
		this.resultTip = resultTip;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
     * 发票代码
     */
    private String invoiceCode;
    /**
     * 发票号码
     */
    private String invoiceNo;
    /**
     * 扫描流水号
     */
    private String invoiceSerialNo;
    /**
     * 购方税号
     */
    private String gfTaxNo;
    /**
     * 购方名称
     */
    private String gfName;
    /**
     * 销方税号
     */
    private String xfTaxNo;
    /**
     * 销方名称
     */
    private String xfName;
    /**
     * 金额
     */
    private BigDecimal invoiceAmount;
    /**
     * 税额
     */
    private BigDecimal taxAmount;
    /**
     * 价税合计
     */
    private BigDecimal totalAmount;
    /**
     * 开票日期
     */
    private Date invoiceDate;
    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 签收方式（0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收，5-pdf上传）
     */
    private String qsType;
    /**
     * 签收结果(0-签收失败 1-签收成功）
     */
    private String qsStatus;
    /**
     * 有效状态 1-有效 0-无效
     */
    private String valid;
    /**
     * 唯一索引 组成：发票代码+发票号码
     */
    private String uuid;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 更新日期
     */
    private Date updateDate;
    /**
     * 签收时间
     */
    private Date qsDate;
    /**
     * 扫描唯一识别码
     */
    private String  scanId;
    /**
     * 扫描备注
     */
    private String notes;
    /**
     * 校验码
     */
    private String checkCode;

    /**
     * 上传的pdf文件名
     */
    private String pdfName;

    /**
     * 解析的pdf是否正常 true：正常  false：解析失败
     */
    private Boolean readPdfSuccess;

    /**
     * 上传的pdf是否重复true:重复   false: 不重复
     */
    private Boolean saveRepeat = Boolean.FALSE;
    /**
     * 查验是否成功
     */
    private Boolean checkSuccess = Boolean.TRUE;
    /**
     * 查验返回信息
     */
    private String resultTip;

    /**
     * 上传图片路径
     */
    private String imgPath;

    @Override
    public Boolean isNullObject() {
        return null;
    }
}
