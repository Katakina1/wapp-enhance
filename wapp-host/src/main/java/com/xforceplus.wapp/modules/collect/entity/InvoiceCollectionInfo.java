package com.xforceplus.wapp.modules.collect.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 发票采集实体（抵账表）
 * @author Colin.hu
 * @date 4/11/2018
 */
@Getter @Setter @ToString
public class InvoiceCollectionInfo extends AbstractBaseDomain {

    private static final long serialVersionUID = -1356483923293195675L;

    /**
     * 税额
     */
    private String taxAmount;

    private String schemaLabel;
	private  String category1;
	private  String category2;

	public String getCategory2() {
		return category2;
	}

	public void setCategory2(String category2) {
		this.category2 = category2;
	}

	public String getCategory1() {
		return category1;
	}

	public void setCategory1(String category1) {
		this.category1 = category1;
	}

    public String getSchemaLabel() {
		return schemaLabel;
	}

	public void setSchemaLabel(String schemaLabel) {
		this.schemaLabel = schemaLabel;
	}

	private Long id;



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getGfTaxNo() {
		return gfTaxNo;
	}

	public void setGfTaxNo(String gfTaxNo) {
		this.gfTaxNo = gfTaxNo;
	}

	public String getDqskssq() {
		return dqskssq;
	}

	public void setDqskssq(String dqskssq) {
		this.dqskssq = dqskssq;
	}

	public String getOutReason() {
		return outReason;
	}

	public void setOutReason(String outReason) {
		this.outReason = outReason;
	}

	public String getXfBankAndNo() {
		return xfBankAndNo;
	}

	public void setXfBankAndNo(String xfBankAndNo) {
		this.xfBankAndNo = xfBankAndNo;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getGxType() {
		return gxType;
	}

	public void setGxType(String gxType) {
		this.gxType = gxType;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public String getGxUserName() {
		return gxUserName;
	}

	public void setGxUserName(String gxUserName) {
		this.gxUserName = gxUserName;
	}

	public String getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getGfAddressAndPhone() {
		return gfAddressAndPhone;
	}

	public void setGfAddressAndPhone(String gfAddressAndPhone) {
		this.gfAddressAndPhone = gfAddressAndPhone;
	}

	public String getRzhType() {
		return rzhType;
	}

	public void setRzhType(String rzhType) {
		this.rzhType = rzhType;
	}

	public String getTxfbz() {
		return txfbz;
	}

	public void setTxfbz(String txfbz) {
		this.txfbz = txfbz;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getOutTaxAmount() {
		return outTaxAmount;
	}

	public void setOutTaxAmount(String outTaxAmount) {
		this.outTaxAmount = outTaxAmount;
	}

	public String getRzhBackMsg() {
		return rzhBackMsg;
	}

	public void setRzhBackMsg(String rzhBackMsg) {
		this.rzhBackMsg = rzhBackMsg;
	}

	public String getXfAddressAndPhone() {
		return xfAddressAndPhone;
	}

	public void setXfAddressAndPhone(String xfAddressAndPhone) {
		this.xfAddressAndPhone = xfAddressAndPhone;
	}

	public Date getRzhDate() {
		return rzhDate;
	}

	public void setRzhDate(Date rzhDate) {
		this.rzhDate = rzhDate;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Date getStatusUpdateDate() {
		return statusUpdateDate;
	}

	public void setStatusUpdateDate(Date statusUpdateDate) {
		this.statusUpdateDate = statusUpdateDate;
	}

	public String getQsStatus() {
		return qsStatus;
	}

	public void setQsStatus(String qsStatus) {
		this.qsStatus = qsStatus;
	}

	public Date getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}

	public String getInvoiceStatus() {
		return invoiceStatus;
	}

	public void setInvoiceStatus(String invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	public String getRzhBelongDate() {
		return rzhBelongDate;
	}

	public void setRzhBelongDate(String rzhBelongDate) {
		this.rzhBelongDate = rzhBelongDate;
	}

	public String getRzhYesorno() {
		return rzhYesorno;
	}

	public void setRzhYesorno(String rzhYesorno) {
		this.rzhYesorno = rzhYesorno;
	}

	public Date getOutDate() {
		return outDate;
	}

	public void setOutDate(Date outDate) {
		this.outDate = outDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRzhBelongDateLate() {
		return rzhBelongDateLate;
	}

	public void setRzhBelongDateLate(String rzhBelongDateLate) {
		this.rzhBelongDateLate = rzhBelongDateLate;
	}

	public String getGxfwq() {
		return gxfwq;
	}

	public void setGxfwq(String gxfwq) {
		this.gxfwq = gxfwq;
	}

	public String getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(String invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public String getXfName() {
		return xfName;
	}

	public void setXfName(String xfName) {
		this.xfName = xfName;
	}

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public String getGfBankAndNo() {
		return gfBankAndNo;
	}

	public void setGfBankAndNo(String gfBankAndNo) {
		this.gfBankAndNo = gfBankAndNo;
	}

	public String getSfdbts() {
		return sfdbts;
	}

	public void setSfdbts(String sfdbts) {
		this.sfdbts = sfdbts;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getGxfwz() {
		return gxfwz;
	}

	public void setGxfwz(String gxfwz) {
		this.gxfwz = gxfwz;
	}

	public String getOutInvoiceAmout() {
		return outInvoiceAmout;
	}

	public void setOutInvoiceAmout(String outInvoiceAmout) {
		this.outInvoiceAmout = outInvoiceAmout;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getDetailYesorno() {
		return detailYesorno;
	}

	public void setDetailYesorno(String detailYesorno) {
		this.detailYesorno = detailYesorno;
	}

	public String getRzlx() {
		return rzlx;
	}

	public void setRzlx(String rzlx) {
		this.rzlx = rzlx;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getGxUserAccount() {
		return gxUserAccount;
	}

	public void setGxUserAccount(String gxUserAccount) {
		this.gxUserAccount = gxUserAccount;
	}

	public String getOutStatus() {
		return outStatus;
	}

	public void setOutStatus(String outStatus) {
		this.outStatus = outStatus;
	}

	public Date getQsDate() {
		return qsDate;
	}

	public void setQsDate(Date qsDate) {
		this.qsDate = qsDate;
	}

	public Date getGxDate() {
		return gxDate;
	}

	public void setGxDate(Date gxDate) {
		this.gxDate = gxDate;
	}

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}

	public String getOutRemark() {
		return outRemark;
	}

	public void setOutRemark(String outRemark) {
		this.outRemark = outRemark;
	}

	public String getGxjzr() {
		return gxjzr;
	}

	public void setGxjzr(String gxjzr) {
		this.gxjzr = gxjzr;
	}

	public String getSfygx() {
		return sfygx;
	}

	public void setSfygx(String sfygx) {
		this.sfygx = sfygx;
	}

	public String getGfName() {
		return gfName;
	}

	public void setGfName(String gfName) {
		this.gfName = gfName;
	}

	public String getQsType() {
		return qsType;
	}

	public void setQsType(String qsType) {
		this.qsType = qsType;
	}

	public String getXfTaxNo() {
		return xfTaxNo;
	}

	public void setXfTaxNo(String xfTaxNo) {
		this.xfTaxNo = xfTaxNo;
	}

	public String getLslbz() {
		return lslbz;
	}

	public void setLslbz(String lslbz) {
		this.lslbz = lslbz;
	}

	public String getMachinecode() {
		return machinecode;
	}

	public void setMachinecode(String machinecode) {
		this.machinecode = machinecode;
	}

	public String getBuyerTaxNo() {
		return buyerTaxNo;
	}

	public void setBuyerTaxNo(String buyerTaxNo) {
		this.buyerTaxNo = buyerTaxNo;
	}

	public String getOpenInvoiceDate() {
		return openInvoiceDate;
	}

	public void setOpenInvoiceDate(String openInvoiceDate) {
		this.openInvoiceDate = openInvoiceDate;
	}

	public String getQsStatusName() {
		return qsStatusName;
	}

	public void setQsStatusName(String qsStatusName) {
		this.qsStatusName = qsStatusName;
	}

	public String getInvoiceStatusName() {
		return invoiceStatusName;
	}

	public void setInvoiceStatusName(String invoiceStatusName) {
		this.invoiceStatusName = invoiceStatusName;
	}

	public String getRzhYesornoName() {
		return rzhYesornoName;
	}

	public void setRzhYesornoName(String rzhYesornoName) {
		this.rzhYesornoName = rzhYesornoName;
	}

	public String getInvoiceTypeName() {
		return invoiceTypeName;
	}

	public void setInvoiceTypeName(String invoiceTypeName) {
		this.invoiceTypeName = invoiceTypeName;
	}

	public String getQsTypeName() {
		return qsTypeName;
	}

	public void setQsTypeName(String qsTypeName) {
		this.qsTypeName = qsTypeName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
     * 购方税号
     */
    private String gfTaxNo;
    private String dqskssq;//当前税款所属期
    private String outReason;//转出原因1-免税项目用 ；2-集体福利,个人消费；3-非正常损失；4-简易计税方法征税项目用；5-免抵退税办法不得抵扣的进项税额；6-纳税检查调减进项税额；7-红字专用发票通知单注明的进项税额；8-上期留抵税额抵减欠税
    private String xfBankAndNo;//销方开户行及账号
    private String uuid;//唯一索引 防重复
    private String gxType;//勾选方式(0-手工勾选 1-扫码勾选 2-导入勾选 3- 智能勾选 4-手工认证 5-扫码认证 6-导入认证)
    private Date sendDate;//发送认证时间
    private String gxUserName;//提交认证操作人
    private String authStatus;//认证处理状态  0-未认证 1-已勾选未确认，2已确认 3 已发送认证 4 认证成功 5 认证失败
    private Date createDate;//采集时间
    private String gfAddressAndPhone;//购方地址电话
    private String rzhType;//认证方式 1-勾选认证 2-扫描认证
    private String txfbz;//通行费标志(Y-可抵扣通行费，N-不可抵扣通行费)
    private String sourceSystem;//底账来源  0-采集 1-查验
    private String outTaxAmount;//转出税额
    private String rzhBackMsg;//认证结果回传信息
    private String xfAddressAndPhone;//销方地址及电话
    private Date rzhDate;//认证时间
    private String totalAmount;//价格合计
    private Date statusUpdateDate;//发票状态修改时间
    private String qsStatus;//签收结果（0-未签收 1-已签收）
    private Date confirmDate;//认证确认时间
    private String invoiceStatus;//发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常
    private String rzhBelongDate;//实际认证归属期 yyyyMM
    private String rzhYesorno;//是否认证 0-未认证 1-已认证
    private Date outDate;//转出日期
    private String remark;//备注
    private String rzhBelongDateLate;//最晚认证归属期 yyyyMM
    private String gxfwq;//当前税款所属期可勾选发票开票日期范围起
    private String invoiceAmount;//金额
    private Date invoiceDate;//开票日期
    private String valid;//是否有效 0-有效 1-无效
    private String xfName;//销方名称
    private String checkCode;//校验码
    private String gfBankAndNo;//购方开户行及账号
    private String sfdbts;//是否代办退税(0：否 1：是)
    private String invoiceType;//发票类型 01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票
    private String gxfwz;//当前税款所属期可勾选发票开票日期范围止
    private String outInvoiceAmout;//转出金额
    private Date lastUpdateDate;//发票状态最后修改时间
    private String detailYesorno;//是否存入明细 0 无明细 1 有明细
    private String rzlx;//认证类型(1-抵扣 2-退税 3-代理退税   发票已认证、已勾选有该标签）
    private String invoiceNo;//发票号码
    private String gxUserAccount;//提交认证操作人账号
    private String outStatus;//转出状态 0-未转出   1-已转出
    private Date qsDate;//签收时间
    private Date gxDate;//勾选时间
    private String invoiceCode;//发票代码
    private String outRemark;//转出备注
    private String gxjzr;//当前税款所属期勾选截止日
    private String sfygx;//是否已勾选
    private String gfName;//购方名称
    private String qsType;//签收方式（0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收5-pdf上传签收
    private String xfTaxNo;//销方税号
    private String lslbz;//零税率标志 (空:非零税率，1:税率栏位显示“免税”，2:税率栏位显示“不征收”，3:零税率
    private String machinecode; //机器编号
	private String taxRate;

	
	/**
     * 抵账表原有购方税号，防止查验后得到的购方税号和原有抵账表购方税号不同而更新不到数据
     */
    private String buyerTaxNo;
    /**
     * 以字符串接收开票日期（只适用于getInvoiceInfo，其他请用invoiceDate字段）
     */
    private String openInvoiceDate;

    /**
     * 签收状态名称
     */
    private String qsStatusName;

    /**
     * 开票状态名称
     */
    private String invoiceStatusName;

    /**
     * 是否签收名称
     */
    private String rzhYesornoName;

    /**
     * 发票类型名称
     */
    private String invoiceTypeName;

    /**
     * 签收方式名称
     */
    private String qsTypeName;

    private String invoiceDateString;

	public String getInvoiceDateString() {
		return invoiceDateString;
	}

	public void setInvoiceDateString(String invoiceDateString) {
		this.invoiceDateString = invoiceDateString;
	}

	@Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
