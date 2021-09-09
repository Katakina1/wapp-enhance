package com.xforceplus.wapp.modules.signin.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 发票签收实体
 * CreateBy leal.liang on 2018/4/12.
 **/

public class RecordInvoiceEntity extends AbstractBaseDomain {

	private static final long serialVersionUID = 3784001228206872166L;
	//修改用
	private Long id;
	private String uuid;

	//扫描时间
	private String scanDate;

	private String authStatus;
	//自定义标志
	private String qs;

	//用户账号
	private String userNum;

	//用户名称
	private String userName;

	//备注
	private String remark;

	//购方税号
	private String gfTaxNo;

	//销方税号
	private String xfTaxNo;

	//合计
	private BigDecimal totalAmount;

	//发票类型
	private String invoiceType;

	//发票代码
	private String invoiceCode;

	//发票号码
	private String invoiceNo;

	//开票日期
	private Date invoiceDate;

	//签收日期
	private Date signInDate;

	//购方名称
	private String gfName;

	//销方名称
	private String xfName;

	//金额
	private BigDecimal invoiceAmount;

	//税额
	private BigDecimal taxAmount;

	//签收方式
	private String qsType;

	//签收描述
	private String notes;

	//扫描id
	private String scanId;

	//签收结果
	private String qsStatus;

	//校验码
	private String checkCode;

	//自定义标识 导入类型（0:excel 1:图片）
	private String importType;
	//重复标识(0：未重复， 1重复)
	private String repeatFlag;
	//自定义签收处理标识，只用于导入时发票已在扫描表存在（1:扫描表存在 2:没有税号处理权限）
	private String handleFlag;

	/**
	 * 发票类型错误标识 1 类型错误
	 */
	private String typeErrorFlag;

	//发票类型名
	private String invoiceTypeName;

	private Integer invoiceStatus;


	private String userAccount;

	private String dyInvoiceCode;
	private String dyInvoiceNo;

	private String scanPathId;




	//序列号
	private String localTrmSeqNum;


	private String billtypeCode;

	private String fileType;

	private String venderid;

	private String companyCode;

	private String jvCode;

	private String venderidEdit;

	private String isExistStamper;

	private String noExistStamperNotes;

	private String scanMatchStatus;

	private String arrayName;

	private String barCode;

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getArrayName() {
		return arrayName;
	}

	public void setArrayName(String arrayName) {
		this.arrayName = arrayName;
	}

	public String getScanMatchStatus() {
		return scanMatchStatus;
	}

	public void setScanMatchStatus(String scanMatchStatus) {
		this.scanMatchStatus = scanMatchStatus;
	}

	public String getNoExistStamperNotes() {
		return noExistStamperNotes;
	}

	public void setNoExistStamperNotes(String noExistStamperNotes) {
		this.noExistStamperNotes = noExistStamperNotes;
	}

	public String getIsExistStamper() {
		return isExistStamper;
	}

	public void setIsExistStamper(String isExistStamper) {
		this.isExistStamper = isExistStamper;
	}

	public String getVenderidEdit() {
		return venderidEdit;
	}

	public void setVenderidEdit(String venderidEdit) {
		this.venderidEdit = venderidEdit;
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

	public String getVenderid() {
		return venderid;
	}

	public void setVenderid(String venderid) {
		this.venderid = venderid;
	}

	public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getBilltypeCode() {
		return billtypeCode;
	}

	public void setBilltypeCode(String billtypeCode) {
		this.billtypeCode = billtypeCode;
	}

	public String getLocalTrmSeqNum() {
		return localTrmSeqNum;
	}

	public void setLocalTrmSeqNum(String localTrmSeqNum) {
		this.localTrmSeqNum = localTrmSeqNum;
	}



	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}

	public String getQs() {
		return qs;
	}

	public void setQs(String qs) {
		this.qs = qs;
	}

	public String getUserNum() {
		return userNum;
	}

	public void setUserNum(String userNum) {
		this.userNum = userNum;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
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

	public Date getSignInDate() {
		return signInDate;
	}

	public void setSignInDate(Date signInDate) {
		this.signInDate = signInDate;
	}

	public String getGfName() {
		return gfName;
	}

	public void setGfName(String gfName) {
		this.gfName = gfName;
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

	public String getQsType() {
		return qsType;
	}

	public void setQsType(String qsType) {
		this.qsType = qsType;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getScanId() {
		return scanId;
	}

	public void setScanId(String scanId) {
		this.scanId = scanId;
	}

	public String getQsStatus() {
		return qsStatus;
	}

	public void setQsStatus(String qsStatus) {
		this.qsStatus = qsStatus;
	}

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public String getImportType() {
		return importType;
	}

	public void setImportType(String importType) {
		this.importType = importType;
	}

	public String getRepeatFlag() {
		return repeatFlag;
	}

	public void setRepeatFlag(String repeatFlag) {
		this.repeatFlag = repeatFlag;
	}

	public String getHandleFlag() {
		return handleFlag;
	}

	public void setHandleFlag(String handleFlag) {
		this.handleFlag = handleFlag;
	}

	public String getTypeErrorFlag() {
		return typeErrorFlag;
	}

	public void setTypeErrorFlag(String typeErrorFlag) {
		this.typeErrorFlag = typeErrorFlag;
	}

	public String getInvoiceTypeName() {
		return invoiceTypeName;
	}

	public void setInvoiceTypeName(String invoiceTypeName) {
		this.invoiceTypeName = invoiceTypeName;
	}

	public Integer getInvoiceStatus() {
		return invoiceStatus;
	}

	public void setInvoiceStatus(Integer invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public String getDyInvoiceCode() {
		return dyInvoiceCode;
	}

	public void setDyInvoiceCode(String dyInvoiceCode) {
		this.dyInvoiceCode = dyInvoiceCode;
	}

	public String getDyInvoiceNo() {
		return dyInvoiceNo;
	}

	public void setDyInvoiceNo(String dyInvoiceNo) {
		this.dyInvoiceNo = dyInvoiceNo;
	}

	public String getScanPathId() {
		return scanPathId;
	}

	public void setScanPathId(String scanPathId) {
		this.scanPathId = scanPathId;
	}

	@Override
	public Boolean isNullObject() {
		return Boolean.FALSE;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getScanDate() {
		return scanDate;
	}

	public void setScanDate(String scanDate) {
		this.scanDate = scanDate;
	}
}
