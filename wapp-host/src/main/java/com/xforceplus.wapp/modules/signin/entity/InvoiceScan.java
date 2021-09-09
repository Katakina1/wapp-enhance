package com.xforceplus.wapp.modules.signin.entity;



public class InvoiceScan {
	private static final long serialVersionUID = -6491365582285157460L;
	/**
	 * 发票扫描id
	 */
	private Long id;
	/**
	 * 发票代码
	 */
	private String invoiceCode;
	/**
	 * 发票号码
	 */
	private String invoiceNo;
	/**
	 * 发票类型
	 */
	private String invoiceType;
	/**
	 *购买方纳税人识别号 
	 */
	private String gfTaxNo;
	/**
	 * 购方名称
	 */
	private String gfName;
	/**
	 * 销售方纳税人识别号
	 */
	private String xfTaxNo;

	/**
	 * 销售名称
	 */
	private String xfName;
	/**
	 * 开票日期
	 */
	private String invoiceDate;
	/**
	 * 发票金额合计
	 */
	private String invoiceAmount;
	/**
	 * 发票税额合计
	 */
	private String taxAmount;
	/**
	 * 发票价税合计
	 */
	private String totalAmount;
	/**
	 * 扫描路径
	 */
	private String scanPath;
	/**
	 * 发票状态
	 */
	private String invoiceStatus;
	/**
	 * 签收状态
	 */
	private String qsStatus;

	/**
	 * 签收方式
	 */
	private String qsType;
	/**
	 * 操作员
	 */
	private String userName;
	/**
	 * 用户id
	 */
	private Long userId;
	/**
	 * 发票流水号
	 */
	private String invoiceSerialNo;
	/**
	 * uuId发票代码+发票号码
	 */
	private String uuId;
	/**
	 * 机构名称
	 *
	 */
	private String companyName;
	/**
	 * 签收时间
	 */
	private String qsDate;

	// 是否需要核对底账
	private Integer  insideOutside;
	/**
	 * 创建时间
	 */
	private String  createDate;
	/**
	 * 扫描唯一识别码
	 */
	private String  scanId;

	/**
	 * 校验码
	 */
	private String checkCode;

	/**
	 * 是否有效（1-有效 0-无效）
	 */
	private String valid;

	/**
	 * 扫描备注
	 */
	private String notes;

	private String dyInvoiceCode;
	private String dyInvoiceNo;

	private String userAccount;

	//序列号
	private String localTrmSeqNum;
	//退单号
	private String rebateno;

	private String fileType;

	private String jvCode;

	private String companyCode;
    private String venderid;


    private String deductibleTax;

	private String deductibleTaxRate;

	private String flowType;

	private String venderidEdit;

	public String getVenderidEdit() {
		return venderidEdit;
	}

	public void setVenderidEdit(String venderidEdit) {
		this.venderidEdit = venderidEdit;
	}

	public String getFlowType() {
		return flowType;
	}

	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}

	public String getDeductibleTax() {
		return deductibleTax;
	}

	public void setDeductibleTax(String deductibleTax) {
		this.deductibleTax = deductibleTax;
	}

	public String getDeductibleTaxRate() {
		return deductibleTaxRate;
	}

	public void setDeductibleTaxRate(String deductibleTaxRate) {
		this.deductibleTaxRate = deductibleTaxRate;
	}

	public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
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

    public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getRebateno() {
		return rebateno;
	}

	public void setRebateno(String rebateno) {
		this.rebateno = rebateno;
	}

	public String getLocalTrmSeqNum() {
		return localTrmSeqNum;
	}

	public void setLocalTrmSeqNum(String localTrmSeqNum) {
		this.localTrmSeqNum = localTrmSeqNum;
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

	public String getScanId() {
		return scanId;
	}

	public void setScanId(String scanId) {
		this.scanId = scanId;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
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

	public String getQsDate() {
		return qsDate;
	}

	public void setQsDate(String qsDate) {
		this.qsDate = qsDate;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getUuId() {
		return uuId;
	}

	public void setUuId(String uuId) {
		this.uuId = uuId;
	}
	public String getInvoiceSerialNo() {
		return invoiceSerialNo;
	}
	public void setInvoiceSerialNo(String invoiceSerialNo) {
		this.invoiceSerialNo = invoiceSerialNo;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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

	public String getQsStatus() {
		return qsStatus;
	}

	public void setQsStatus(String qsStatus) {
		this.qsStatus = qsStatus;
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
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
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

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public String getXfName() {
		return xfName;
	}

	public void setXfName(String xfName) {
		this.xfName = xfName;
	}

	public String getQsType() {
		return qsType;
	}

	public void setQsType(String qsType) {
		this.qsType = qsType;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
}
