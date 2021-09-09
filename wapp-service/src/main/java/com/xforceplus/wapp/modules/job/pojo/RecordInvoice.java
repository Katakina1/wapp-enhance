package com.xforceplus.wapp.modules.job.pojo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 发票底账实体 Created by 田继东 on 2016/11/24
 */
public class RecordInvoice extends BasePojo {
	private static final long serialVersionUID = -2496130397993637282L;

	/**************************************** field *****************************************/

	/**
	 * 主键id
	 */
	private Long id;

	/**
	 * 发票类型
	 * 
	 */
	private String invoiceType;

	/**
	 * 发票代码
	 */
	private String invoiceCode;

	/**
	 * 发票号码
	 */
	private String invoiceNo;

	/**
	 * 开票日期
	 */
	private Date invoiceDate;

	/**
	 * 购方税号
	 */
	private String gfTaxNo;
	/**
	 * 票面购方税号
	 */
	private String newGfTaxNo;

	/**
	 * 购方名称
	 */
	private String gfName;

	/**
	 * 购方地址及电话
	 */
	private String gfAddressAndPhone;

	/**
	 * 购方开户行及银行账号
	 */
	private String gfBankAndNo;

	/**
	 * 销方税号
	 */
	private String xfTaxNo;

	/**
	 * 销方名称
	 */
	private String xfName;

	/**
	 * 销方地址电话
	 */
	private String xfAddressAndPhone;

	/**
	 * 销方开户行及电话
	 */
	private String xfBankAndNo;

	/**
	 * 金额
	 */
	private BigDecimal invoiceAmount;

	/**
	 * 税额
	 */
	private BigDecimal taxAmount;

	/**
	 * 价格合计
	 */
	private BigDecimal totalAmount;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 底账状态
	 */
	private String invoiceStatus;
	/**
	 * 底账状态 最后更新时间
	 */
	private Date last_update_date;

	/**
	 * 发票处理状态
	 */
	private String handleState;
	/**
	 * 认证时间
	 */
	private Date confirm_date;
	/**
	 * 签收时间
	 */
	private Date qs_date;
	/**
	 * 最后一个有效归属期 格式 yyyyMM
	 */
	private Integer rzh_belong_date_late;
	/**
	 * 实际认证的归属期 格式 yyyyMM
	 */
	private Integer rzh_belong_date;
	/**
	 * 认证方式
	 */
	private String rzh_type;
	/**
	 * 是否认证
	 */
	private String rzhYesorno;
	/**
	 * 操作人
	 */
	private String gxUserAccount;
	private String gxUserName;
	/**
	 * 扫描路径
	 */
	private Long scanPathId;
	private String scanPathName;

	/**
	 * 底账来源
	 */
	private String sourceSystem;

	/**
	 * 是否有效
	 * 
	 */
	private String valid;

	private String uuid;
	/**
	 * 最后一个有效归属期 格式 yyyyMM
	 */
	private Integer rzhBelongDateLate;

	/**
	 * 最后一次修改时间
	 */
	private Date updateDate;

	/**
	 * 数据产生日期
	 */
	private Date createDate;
	/**
	 * 当前税款所属期
	 */
	private String dqskssq;
	/**
	 * 当前税款所属期勾选截止日
	 */
	private String gxjzr;
	/**
	 * 当前税款所属期可勾选发票开票日期范围起
	 */
	private String gxfwq;
	/**
	 * 当前税款所属期可勾选发票开票日期范围止
	 */
	private String gxfwz;
	/**
	 * 是否已勾选 0 勾选 1 未勾选
	 */
	private String sfygx;
	/**
	 * 是否存入明细 0 无明细 1 有明细
	 */
	private int detailYseorno;
	/**
	 * 是否推送erp 0未推送 1 已推送
	 */
	private int erpPushYseorno;
	/**
	 * 明细是否推送 0 未推送 1 已推送
	 */
	private int detailErpPushYseorno;
	/**
	 * 发票签收处理状态
	 */
	private String authStatus;
	/**
	 * 发票认证处理状态
	 */
	private String scanStatus;
	
	/**
	 * 数据产生日期+数据产生序号
	 */
	private String fileUuid;
	
	
	/**
	 * 校验码
	 */
	private String checkCode;

	private String sfdbts;
	private String rzlx;
	List<RecordInvoiceDetail> detailList = new ArrayList<RecordInvoiceDetail>();
	List<VehicleSaleInvoices> vehicList = new ArrayList<VehicleSaleInvoices>();
//	List<TransportInvoices> tranList = new ArrayList<TransportInvoices>();
//	List<TransportInvoiceDetail> trdetailList = new ArrayList<TransportInvoiceDetail>();
	
	private Date handleDate;
	
	private  String handleCode;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getNewGfTaxNo() {
		return newGfTaxNo;
	}

	public void setNewGfTaxNo(String newGfTaxNo) {
		this.newGfTaxNo = newGfTaxNo;
	}

	public String getGfName() {
		return gfName;
	}

	public void setGfName(String gfName) {
		this.gfName = gfName;
	}

	public String getGfAddressAndPhone() {
		return gfAddressAndPhone;
	}

	public void setGfAddressAndPhone(String gfAddressAndPhone) {
		this.gfAddressAndPhone = gfAddressAndPhone;
	}

	public String getGfBankAndNo() {
		return gfBankAndNo;
	}

	public void setGfBankAndNo(String gfBankAndNo) {
		this.gfBankAndNo = gfBankAndNo;
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

	public String getXfAddressAndPhone() {
		return xfAddressAndPhone;
	}

	public void setXfAddressAndPhone(String xfAddressAndPhone) {
		this.xfAddressAndPhone = xfAddressAndPhone;
	}

	public String getXfBankAndNo() {
		return xfBankAndNo;
	}

	public void setXfBankAndNo(String xfBankAndNo) {
		this.xfBankAndNo = xfBankAndNo;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getInvoiceStatus() {
		return invoiceStatus;
	}

	public void setInvoiceStatus(String invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	public Date getLast_update_date() {
		return last_update_date;
	}

	public void setLast_update_date(Date last_update_date) {
		this.last_update_date = last_update_date;
	}

	public String getHandleState() {
		return handleState;
	}

	public void setHandleState(String handleState) {
		this.handleState = handleState;
	}

	public Date getConfirm_date() {
		return confirm_date;
	}

	public void setConfirm_date(Date confirm_date) {
		this.confirm_date = confirm_date;
	}

	public Date getQs_date() {
		return qs_date;
	}

	public void setQs_date(Date qs_date) {
		this.qs_date = qs_date;
	}

	public Integer getRzh_belong_date_late() {
		return rzh_belong_date_late;
	}

	public void setRzh_belong_date_late(Integer rzh_belong_date_late) {
		this.rzh_belong_date_late = rzh_belong_date_late;
	}

	public Integer getRzh_belong_date() {
		return rzh_belong_date;
	}

	public void setRzh_belong_date(Integer rzh_belong_date) {
		this.rzh_belong_date = rzh_belong_date;
	}

	public String getRzh_type() {
		return rzh_type;
	}

	public void setRzh_type(String rzh_type) {
		this.rzh_type = rzh_type;
	}

	public String getRzhYesorno() {
		return rzhYesorno;
	}

	public void setRzhYesorno(String rzhYesorno) {
		this.rzhYesorno = rzhYesorno;
	}

	public String getGxUserAccount() {
		return gxUserAccount;
	}

	public void setGxUserAccount(String gxUserAccount) {
		this.gxUserAccount = gxUserAccount;
	}

	public String getGxUserName() {
		return gxUserName;
	}

	public void setGxUserName(String gxUserName) {
		this.gxUserName = gxUserName;
	}

	public Long getScanPathId() {
		return scanPathId;
	}

	public void setScanPathId(Long scanPathId) {
		this.scanPathId = scanPathId;
	}

	public String getScanPathName() {
		return scanPathName;
	}

	public void setScanPathName(String scanPathName) {
		this.scanPathName = scanPathName;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
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

	public Integer getRzhBelongDateLate() {
		return rzhBelongDateLate;
	}

	public void setRzhBelongDateLate(Integer rzhBelongDateLate) {
		this.rzhBelongDateLate = rzhBelongDateLate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getDqskssq() {
		return dqskssq;
	}

	public void setDqskssq(String dqskssq) {
		this.dqskssq = dqskssq;
	}

	public String getGxjzr() {
		return gxjzr;
	}

	public void setGxjzr(String gxjzr) {
		this.gxjzr = gxjzr;
	}

	public String getGxfwq() {
		return gxfwq;
	}

	public void setGxfwq(String gxfwq) {
		this.gxfwq = gxfwq;
	}

	public String getGxfwz() {
		return gxfwz;
	}

	public void setGxfwz(String gxfwz) {
		this.gxfwz = gxfwz;
	}

	public String getSfygx() {
		return sfygx;
	}

	public void setSfygx(String sfygx) {
		this.sfygx = sfygx;
	}

	public int getDetailYseorno() {
		return detailYseorno;
	}

	public void setDetailYseorno(int detailYseorno) {
		this.detailYseorno = detailYseorno;
	}

	public int getErpPushYseorno() {
		return erpPushYseorno;
	}

	public void setErpPushYseorno(int erpPushYseorno) {
		this.erpPushYseorno = erpPushYseorno;
	}

	public int getDetailErpPushYseorno() {
		return detailErpPushYseorno;
	}

	public void setDetailErpPushYseorno(int detailErpPushYseorno) {
		this.detailErpPushYseorno = detailErpPushYseorno;
	}

	public String getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}

	public String getScanStatus() {
		return scanStatus;
	}

	public void setScanStatus(String scanStatus) {
		this.scanStatus = scanStatus;
	}

	public String getFileUuid() {
		return fileUuid;
	}

	public void setFileUuid(String fileUuid) {
		this.fileUuid = fileUuid;
	}

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public String getSfdbts() {
		return sfdbts;
	}

	public void setSfdbts(String sfdbts) {
		this.sfdbts = sfdbts;
	}

	public String getRzlx() {
		return rzlx;
	}

	public void setRzlx(String rzlx) {
		this.rzlx = rzlx;
	}

	public List<RecordInvoiceDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<RecordInvoiceDetail> detailList) {
		this.detailList = detailList;
	}

	public List<VehicleSaleInvoices> getVehicList() {
		return vehicList;
	}

	public void setVehicList(List<VehicleSaleInvoices> vehicList) {
		this.vehicList = vehicList;
	}

	public Date getHandleDate() {
		return handleDate;
	}

	public void setHandleDate(Date handleDate) {
		this.handleDate = handleDate;
	}

	public String getHandleCode() {
		return handleCode;
	}

	public void setHandleCode(String handleCode) {
		this.handleCode = handleCode;
	}
}
