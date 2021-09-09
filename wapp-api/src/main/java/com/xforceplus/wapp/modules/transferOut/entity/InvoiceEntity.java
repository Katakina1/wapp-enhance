package com.xforceplus.wapp.modules.transferOut.entity;


import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/11
 * Time:19:26
 * 抵账表
*/
@Getter @Setter @ToString
public class InvoiceEntity extends AbstractBaseDomain {
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
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

	public Double getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(Double invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public Double getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(Double taxAmount) {
		this.taxAmount = taxAmount;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
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

	public Date getStatusUpdateDate() {
		return statusUpdateDate;
	}

	public void setStatusUpdateDate(Date statusUpdateDate) {
		this.statusUpdateDate = statusUpdateDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public Date getRzhDate() {
		return rzhDate;
	}

	public void setRzhDate(Date rzhDate) {
		this.rzhDate = rzhDate;
	}

	public Date getQsDate() {
		return qsDate;
	}

	public void setQsDate(Date qsDate) {
		this.qsDate = qsDate;
	}

	public String getRzhBelongDateLate() {
		return rzhBelongDateLate;
	}

	public void setRzhBelongDateLate(String rzhBelongDateLate) {
		this.rzhBelongDateLate = rzhBelongDateLate;
	}

	public String getRzhBelongDate() {
		return rzhBelongDate;
	}

	public void setRzhBelongDate(String rzhBelongDate) {
		this.rzhBelongDate = rzhBelongDate;
	}

	public String getConfirmUser() {
		return confirmUser;
	}

	public void setConfirmUser(String confirmUser) {
		this.confirmUser = confirmUser;
	}

	public Date getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}

	public String getMachinecode() {
		return machinecode;
	}

	public void setMachinecode(String machinecode) {
		this.machinecode = machinecode;
	}

	public String getRzhType() {
		return rzhType;
	}

	public void setRzhType(String rzhType) {
		this.rzhType = rzhType;
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getGxDate() {
		return gxDate;
	}

	public void setGxDate(Date gxDate) {
		this.gxDate = gxDate;
	}

	public String getRzhBackMsg() {
		return rzhBackMsg;
	}

	public void setRzhBackMsg(String rzhBackMsg) {
		this.rzhBackMsg = rzhBackMsg;
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

	public String getDetailYesorno() {
		return detailYesorno;
	}

	public void setDetailYesorno(String detailYesorno) {
		this.detailYesorno = detailYesorno;
	}

	public String getGxType() {
		return gxType;
	}

	public void setGxType(String gxType) {
		this.gxType = gxType;
	}

	public String getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public String getRzlx() {
		return rzlx;
	}

	public void setRzlx(String rzlx) {
		this.rzlx = rzlx;
	}

	public String getSfdbts() {
		return sfdbts;
	}

	public void setSfdbts(String sfdbts) {
		this.sfdbts = sfdbts;
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

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public String getTxfbz() {
		return txfbz;
	}

	public void setTxfbz(String txfbz) {
		this.txfbz = txfbz;
	}

	public String getLslbz() {
		return lslbz;
	}

	public void setLslbz(String lslbz) {
		this.lslbz = lslbz;
	}

	public String getOutStatus() {
		return outStatus;
	}

	public void setOutStatus(String outStatus) {
		this.outStatus = outStatus;
	}

	public Double getOutInvoiceAmout() {
		return outInvoiceAmout;
	}

	public void setOutInvoiceAmout(Double outInvoiceAmout) {
		this.outInvoiceAmout = outInvoiceAmout;
	}

	public Double getOutTaxAmount() {
		return outTaxAmount;
	}

	public void setOutTaxAmount(Double outTaxAmount) {
		this.outTaxAmount = outTaxAmount;
	}

	public String getOutReason() {
		return outReason;
	}

	public void setOutReason(String outReason) {
		this.outReason = outReason;
	}

	public String getOutRemark() {
		return outRemark;
	}

	public void setOutRemark(String outRemark) {
		this.outRemark = outRemark;
	}

	public Date getOutDate() {
		return outDate;
	}

	public void setOutDate(Date outDate) {
		this.outDate = outDate;
	}

	public String getOutBy() {
		return outBy;
	}

	public void setOutBy(String outBy) {
		this.outBy = outBy;
	}

	public String getQsBy() {
		return qsBy;
	}

	public void setQsBy(String qsBy) {
		this.qsBy = qsBy;
	}

	public String getStringTotalAmount() {
		return stringTotalAmount;
	}

	public void setStringTotalAmount(String stringTotalAmount) {
		this.stringTotalAmount = stringTotalAmount;
	}

	private Long id;//
    private Long invoiceId;//进项税转出模块用于保存抵账表id
    private String invoiceType;//发票类型（01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票）
    private String invoiceCode;//发票代码
    private String invoiceNo;//发票号码
    private Date invoiceDate;//开票时间
    private String gfTaxNo;//购方税号
    private String gfName;//购方名称
    private String gfAddressAndPhone;//购方地址电话
    private String gfBankAndNo;//购方银行账号
    private String xfTaxNo;//销方税号
    private String xfName;//销方名称
    private String xfAddressAndPhone;//销方地址电话
    private String xfBankAndNo;//销方银行账号
    private Double invoiceAmount;//不含税金额（保留2位小数）
    private Double taxAmount;//发票税额（保留2位小数）
    private Double totalAmount;//价税合计（保留2位小数）
    private String remark;//发票备注
    private String invoiceStatus;//发票状态（0-正常  1-失控 2-作废  3-红冲 4-异常）
    private Date statusUpdateDate;//发票状态修改时间
    private Date lastUpdateDate;//发票状态最后修改时间
    private Date rzhDate;//认证时间
    private Date qsDate;//签收时间
    private String rzhBelongDateLate;//最晚认证归属期 yyyyMM
    private String rzhBelongDate;//认证归属期
    private String confirmUser;//确认人
    private Date confirmDate;//认证确认时间
    private String machinecode;//机器编号
    private String rzhType;//认证方式 1-勾选认证 2-扫描认证
    private String rzhYesorno;//是否认证 0-未认证 1-已认证
    private String gxUserAccount;//提交认证操作人账号
    private String gxUserName;//提交认证操作人
    private String sourceSystem;//底账来源（0-采集 1-查验）
    private String valid;//是否有效（0-有效 1-无效）
    private String uuid;//发票唯一字段 组成：发票代码+发票号码
    private Date createDate;//采集时间
    private Date gxDate;//勾选时间
    private String rzhBackMsg;//认证结果回传信息
    private String dqskssq;//当前税款所属期
    private String gxjzr;//当前税款所属期勾选截止日
    private String gxfwq;//当前税款所属期可勾选发票开票日期范围起
    private String gxfwz;//当前税款所属期可勾选发票开票日期范围止
    private String sfygx;//是否已勾选
    private String detailYesorno;//是否存入明细 0 无明细 1 有明细
    private String gxType;//勾选方式(0-手工勾选 1-扫码勾选 2-导入a选 3- 智能勾选 4-手工认证 5-扫码认证 6-导入认证)
    private String authStatus;//认证处理状态  0-未认证 1-已勾选未确认，2已确认 3 已发送认证 4 认证成功 5 认证失败
    private Date sendDate;//发送认证时间
    private String rzlx;//认证类型(1-抵扣 2-退税 3-代理退税   发票已认证、已勾选有该标签）
    private String sfdbts;//是否代办退税(0：否 1：是)
    private String qsType;//签收方式（0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收5-pdf上传签收
    private String qsStatus;//签收结果（0-未签收 1-已签收）
    private String checkCode;//校验码
    private String txfbz;//通行费标志(Y-可抵扣通行费，N-不可抵扣通行费)
    private String lslbz;//零税率标志 (空:非零税率，1:税率栏位显示“免税”，2:税率栏位显示“不征收”，3:零税率
    private String outStatus;//转出状态 0-未转出   1-已转出
    private Double outInvoiceAmout;//转出金额
    private Double outTaxAmount;//转出税额
    private String outReason;//转出原因1-免税项目用 ；2-集体福利,个人消费；3-非正常损失；4-简易计税方法征税项目用；5-免抵退税办法不得抵扣的进项税额；6-纳税检查调减进项税额；7-红字专用发票通知单注明的进项税额；8-上期留抵税额抵减欠税
    private String outRemark;//转出备注
    private Date outDate;//转出日期
    private String outBy;//转出人
    private String qsBy;//签收人
    private String stringTotalAmount;//用来存储大写的价税合计

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}