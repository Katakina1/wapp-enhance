package com.xforceplus.wapp.modules.einvoice.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created on 2018/04/19.
 * @author marvin
 * 底账表实体类
 */
@Getter
@Setter
@ToString
public class RecordInvoice extends AbstractBaseDomain {

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

	public String getQsDate() {
		return qsDate;
	}

	public void setQsDate(String qsDate) {
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

	public String getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(String confirmDate) {
		this.confirmDate = confirmDate;
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

	public String getGxDate() {
		return gxDate;
	}

	public void setGxDate(String gxDate) {
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

	public String getSendDate() {
		return sendDate;
	}

	public void setSendDate(String sendDate) {
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

	public BigDecimal getOutInvoiceAmout() {
		return outInvoiceAmout;
	}

	public void setOutInvoiceAmout(BigDecimal outInvoiceAmout) {
		this.outInvoiceAmout = outInvoiceAmout;
	}

	public BigDecimal getOutTaxAmount() {
		return outTaxAmount;
	}

	public void setOutTaxAmount(BigDecimal outTaxAmount) {
		this.outTaxAmount = outTaxAmount;
	}

	public String getOutReason() {
		return outReason;
	}

	public void setOutReason(String outReason) {
		this.outReason = outReason;
	}

	public Date getOutDate() {
		return outDate;
	}

	public void setOutDate(Date outDate) {
		this.outDate = outDate;
	}

	public String getOutRemark() {
		return outRemark;
	}

	public void setOutRemark(String outRemark) {
		this.outRemark = outRemark;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<RecordInvoiceDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<RecordInvoiceDetail> detailList) {
		this.detailList = detailList;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultTip() {
		return resultTip;
	}

	public void setResultTip(String resultTip) {
		this.resultTip = resultTip;
	}

	public String getMachinecode() {
		return machinecode;
	}

	public void setMachinecode(String machinecode) {
		this.machinecode = machinecode;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = -3786687506558252383L;

    /**
     * 发票类型 01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票
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
     * 购方名称
     */
    private String gfName;

    /**
     * 购方地址电话
     */
    private String gfAddressAndPhone;

    /**
     * 购方开户行及账号
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
     * 销方地址及电话
     */
    private String xfAddressAndPhone;

    /**
     * 销方开户行及账号
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
     * 发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常
     */
    private String invoiceStatus;

    /**
     * 发票状态修改时间
     */
    private Date statusUpdateDate;

    /**
     * 发票状态最后修改时间
     */
    private Date lastUpdateDate;

    /**
     * 认证时间
     */
    private Date rzhDate;

    /**
     * 签收时间
     */
    private String qsDate;

    /**
     * 最晚认证归属期 yyyyMM
     */
    private String rzhBelongDateLate;

    /**
     * 实际认证归属期 yyyyMM
     */
    private String rzhBelongDate;

    /**
     * 认证确认时间
     */
    private String confirmDate;

    /**
     * 认证方式 1-勾选认证 2-扫描认证
     */
    private String rzhType;

    /**
     * 是否认证 0-未认证 1-已认证
     */
    private String rzhYesorno;

    /**
     * 提交认证操作人账号
     */
    private String gxUserAccount;

    /**
     * 提交认证操作人
     */
    private String gxUserName;

    /**
     * 底账来源  0-采集 1-查验
     */
    private String sourceSystem;

    /**
     * 是否有效 1-有效 0-无效
     */
    private String valid;

    /**
     * 发票代码+发票号码    唯一索引 防重复
     */
    private String uuid;

    /**
     * 采集时间
     */
    private Date createDate;

    /**
     * 勾选时间
     */
    private String gxDate;

    /**
     * 认证结果回传信息
     */
    private String rzhBackMsg;

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
     * 是否已勾选
     */
    private String sfygx;

    /**
     * 是否存入明细 0 无明细 1 有明细
     */
    private String detailYesorno;

    /**
     * 勾选方式(0-手工勾选 1-扫码勾选 2-导入勾选 3- 智能勾选 4-手工认证 5-扫码认证 6-导入认证)
     */
    private String gxType;

    /**
     * 认证处理状态  0-未认证 1-已勾选未确认，2已确认 3 已发送认证 4 认证成功 5 认证失败
     */
    private String authStatus;

    /**
     * 发送认证时间
     */
    private String sendDate;

    /**
     * 认证类型(1-抵扣 2-退税 3-代理退税   发票已认证、已勾选有该标签）
     */
    private String rzlx;

    /**
     * 是否代办退税(0：否 1：是)
     */
    private String sfdbts;

    /**
     * 签收方式（0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收5-pdf上传签收
     */
    private String qsType;

    /**
     * 签收结果（0-未签收 1-已签收）
     */
    private String qsStatus;

    /**
     * 校验码
     */
    private String checkCode;

    /**
     * 通行费标志(Y-可抵扣通行费，N-不可抵扣通行费)
     */
    private String txfbz;

    /**
     * 零税率标志 (空:非零税率，1:税率栏位显示“免税”，2:税率栏位显示“不征收”，3:零税率
     */
    private String lslbz;

    /**
     * 转出状态 0-未转出   1-全部转出  2-部分转出
     */
    private String outStatus;

    /**
     * 转出金额
     */
    private BigDecimal outInvoiceAmout;

    /**
     * 转出税额
     */
    private BigDecimal outTaxAmount;

    /**
     * 转出原因1-免税项目用 ；2-集体福利,个人消费；
     * 3-非正常损失；4-简易计税方法征税项目用；
     * 5-免抵退税办法不得抵扣的进项税额；
     * 6-纳税检查调减进项税额；
     * 7-红字专用发票通知单注明的进项税额；
     * 8-上期留抵税额抵减欠税
     */
    private String outReason;

    /**
     * 转出日期
     */
    private Date outDate;

    /**
     * 转出备注
     */
    private String outRemark;

    /**
     * 备注
     */
    private String remark;

    /**
     * 底账表明细列表
     */
    private List<RecordInvoiceDetail> detailList;

    /**
     * 查询回来的状态码
     */
    private String resultCode;

    /**
     * 查验回来的错误信息
     */
    private String resultTip;

    /**
     * 机器编码
     */
    private String machinecode;

    @Override
    public Boolean isNullObject() {
        return null;
    }
}
