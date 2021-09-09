package com.xforceplus.wapp.modules.certification.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * 发票认证实体类
 * @author kevin.wang
 * @date 4/13/2018
 */
@Getter
@Setter
@ToString
public class InvoiceCertificationEntity {

    //税号权限-当前税号下的发票是否有底账信息
    private Boolean taxAccess;

    public Boolean getTaxAccess() {
		return taxAccess;
	}

	public void setTaxAccess(Boolean taxAccess) {
		this.taxAccess = taxAccess;
	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
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

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
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

	public String getStatusUpdateDate() {
		return statusUpdateDate;
	}

	public void setStatusUpdateDate(String statusUpdateDate) {
		this.statusUpdateDate = statusUpdateDate;
	}

	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getRzhDate() {
		return rzhDate;
	}

	public void setRzhDate(String rzhDate) {
		this.rzhDate = rzhDate;
	}

	public String getQsDate() {
		return qsDate;
	}

	public void setQsDate(String qsDate) {
		this.qsDate = qsDate;
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

	public String getRzhYesOrNo() {
		return rzhYesOrNo;
	}

	public void setRzhYesOrNo(String rzhYesOrNo) {
		this.rzhYesOrNo = rzhYesOrNo;
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

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public String getGxDate() {
		return gxDate;
	}

	public void setGxDate(String gxDate) {
		this.gxDate = gxDate;
	}

	public String getSfygx() {
		return sfygx;
	}

	public void setSfygx(String sfygx) {
		this.sfygx = sfygx;
	}

	public String getDetailYesOrNo() {
		return detailYesOrNo;
	}

	public void setDetailYesOrNo(String detailYesOrNo) {
		this.detailYesOrNo = detailYesOrNo;
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

	public String getRzhBackMsg() {
		return rzhBackMsg;
	}

	public void setRzhBackMsg(String rzhBackMsg) {
		this.rzhBackMsg = rzhBackMsg;
	}

	public String getCurrentTaxPeriod() {
		return currentTaxPeriod;
	}

	public void setCurrentTaxPeriod(String currentTaxPeriod) {
		this.currentTaxPeriod = currentTaxPeriod;
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

	public String getScanCode() {
		return scanCode;
	}

	public void setScanCode(String scanCode) {
		this.scanCode = scanCode;
	}

	private List<Long> ids;

    private Integer count;
    //智能勾选操作状态
    private String flag;
    //操作状态码
    private Integer code;

    //底账表Id
    private Long id;

    //发票类型 01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票
    private String invoiceType;

    //发票代码
    private String invoiceCode;

    //发票号码
    private String invoiceNo;

    //开票时间
    private String invoiceDate;

    //购方名称
    private String gfName;

    //销方名称
    private String xfName;

    //不含税金额
    private BigDecimal invoiceAmount;

    //发票税额
    private BigDecimal taxAmount;

    //价格合计
    private BigDecimal totalAmount;

    //发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常
    private String invoiceStatus;

    //发票状态修改时间
    private String  statusUpdateDate;

    //发票状态最后修改时间
    private String lastUpdateDate;

    //认证时间
    private String rzhDate;

    //签收时间
    private String qsDate;

    //认证确认时间
    private String confirmDate;

    //认证方式 1-勾选认证 2-扫描认证
    private String rzhType;

    //是否认证 0-未认证 1-已认证
    private String rzhYesOrNo;

    //提交认证操作人账号
    private String gxUserAccount;

    //提交认证操作人
    private String gxUserName;

    //是否有效 0-有效 1-无效
    private String valid;

    //勾选时间
    private String gxDate;

    //是否已勾选
    private String sfygx;

    //是否存入明细 0 无明细 1 有明细
    private String detailYesOrNo;

    //勾选方式(0-手工勾选 1-扫码勾选 2-导入勾选 3- 智能勾选 4-手工认证 5-扫码认证 6-导入认证)
    private String gxType;

    //认证处理状态  0-未认证 1-已勾选未确认，2已确认 3 已发送认证 4 认证成功 5 认证失败
    private String authStatus;

    //发送认证时间
    private String sendDate;

    //认证类型(1-抵扣 2-退税 3-代理退税   发票已认证、已勾选有该标签）
    private String rzlx;

    //签收方式（0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收
    private String qsType;

    //签收结果（0-未签收 1-已签收）
    private String  qsStatus;

    //校验码
    private String checkCode;

    //认证结果回传信息
    private String rzhBackMsg;

    //当前税款所属期--取自t_dx_tax_current表
    private String currentTaxPeriod;

    //当前税款所属期勾选截止日
    private String gxjzr;

    //当前税款所属期可勾选发票开票日期范围起
    private String gxfwq;

    //当前税款所属期可勾选发票开票日期范围止
    private String gxfwz;

    //最晚认证归属期 yyyyMM
    private String rzhBelongDateLate;

    //实际认证归属期 yyyyMM
    private String rzhBelongDate;

    private String scanCode;

}
