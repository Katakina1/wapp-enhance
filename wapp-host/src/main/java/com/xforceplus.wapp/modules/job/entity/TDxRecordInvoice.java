package com.xforceplus.wapp.modules.job.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TDxRecordInvoice implements Serializable {
    private Long id;

    private String invoiceType;

    private String invoiceCode;

    private String invoiceNo;

    private Date invoiceDate;

    private String gfTaxNo;

    private String gfName;

    private String gfAddressAndPhone;

    private String gfBankAndNo;

    private String xfTaxNo;

    private String xfName;

    private String xfAddressAndPhone;

    private String xfBankAndNo;

    private BigDecimal invoiceAmount;

    private BigDecimal taxAmount;

    private BigDecimal totalAmount;

    private String invoiceStatus;

    private Date statusUpdateDate;

    private Date lastUpdateDate;

    private Date rzhDate;

    private Date qsDate;

    private String rzhBelongDateLate;

    private String rzhBelongDate;

    private Date confirmDate;

    private String rzhType;

    private String rzhYesorno;

    private String gxUserAccount;

    private String gxUserName;

    private String sourceSystem;

    private String valid;

    private String uuid;

    private Date createDate;

    private Date gxDate;

    private String rzhBackMsg;

    private String dqskssq;

    private String gxjzr;

    private String gxfwq;

    private String gxfwz;

    private String sfygx;

    private String detailYesorno;

    private String gxType;

    private String authStatus;

    private Date sendDate;

    private String rzlx;

    private String sfdbts;

    private String qsType;

    private String qsStatus;

    private String checkCode;

    private String txfbz;

    private String lslbz;

    private String outStatus;

    private BigDecimal outInvoiceAmout;

    private BigDecimal outTaxAmount;

    private String outReason;

    private Date outDate;

    private String remark;

    private String taxRate;

    private String newGfTaxno;

    private String companyCode;

    private String jvcode;

    private String yxse;

    private String xxly;

    private String glzt;

    private String yclx;

    private String yqkgxbz;

    private String category1;
    private String category2;

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        this.invoiceType = invoiceType == null ? null : invoiceType.trim();
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode == null ? null : invoiceCode.trim();
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo == null ? null : invoiceNo.trim();
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
        this.gfTaxNo = gfTaxNo == null ? null : gfTaxNo.trim();
    }

    public String getGfName() {
        return gfName;
    }

    public void setGfName(String gfName) {
        this.gfName = gfName == null ? null : gfName.trim();
    }

    public String getGfAddressAndPhone() {
        return gfAddressAndPhone;
    }

    public void setGfAddressAndPhone(String gfAddressAndPhone) {
        this.gfAddressAndPhone = gfAddressAndPhone == null ? null : gfAddressAndPhone.trim();
    }

    public String getGfBankAndNo() {
        return gfBankAndNo;
    }

    public void setGfBankAndNo(String gfBankAndNo) {
        this.gfBankAndNo = gfBankAndNo == null ? null : gfBankAndNo.trim();
    }

    public String getXfTaxNo() {
        return xfTaxNo;
    }

    public void setXfTaxNo(String xfTaxNo) {
        this.xfTaxNo = xfTaxNo == null ? null : xfTaxNo.trim();
    }

    public String getXfName() {
        return xfName;
    }

    public void setXfName(String xfName) {
        this.xfName = xfName == null ? null : xfName.trim();
    }

    public String getXfAddressAndPhone() {
        return xfAddressAndPhone;
    }

    public void setXfAddressAndPhone(String xfAddressAndPhone) {
        this.xfAddressAndPhone = xfAddressAndPhone == null ? null : xfAddressAndPhone.trim();
    }

    public String getXfBankAndNo() {
        return xfBankAndNo;
    }

    public void setXfBankAndNo(String xfBankAndNo) {
        this.xfBankAndNo = xfBankAndNo == null ? null : xfBankAndNo.trim();
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
        this.invoiceStatus = invoiceStatus == null ? null : invoiceStatus.trim();
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
        this.rzhBelongDateLate = rzhBelongDateLate == null ? null : rzhBelongDateLate.trim();
    }

    public String getRzhBelongDate() {
        return rzhBelongDate;
    }

    public void setRzhBelongDate(String rzhBelongDate) {
        this.rzhBelongDate = rzhBelongDate == null ? null : rzhBelongDate.trim();
    }

    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }

    public String getRzhType() {
        return rzhType;
    }

    public void setRzhType(String rzhType) {
        this.rzhType = rzhType == null ? null : rzhType.trim();
    }

    public String getRzhYesorno() {
        return rzhYesorno;
    }

    public void setRzhYesorno(String rzhYesorno) {
        this.rzhYesorno = rzhYesorno == null ? null : rzhYesorno.trim();
    }

    public String getGxUserAccount() {
        return gxUserAccount;
    }

    public void setGxUserAccount(String gxUserAccount) {
        this.gxUserAccount = gxUserAccount == null ? null : gxUserAccount.trim();
    }

    public String getGxUserName() {
        return gxUserName;
    }

    public void setGxUserName(String gxUserName) {
        this.gxUserName = gxUserName == null ? null : gxUserName.trim();
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem == null ? null : sourceSystem.trim();
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid == null ? null : valid.trim();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
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
        this.rzhBackMsg = rzhBackMsg == null ? null : rzhBackMsg.trim();
    }

    public String getDqskssq() {
        return dqskssq;
    }

    public void setDqskssq(String dqskssq) {
        this.dqskssq = dqskssq == null ? null : dqskssq.trim();
    }

    public String getGxjzr() {
        return gxjzr;
    }

    public void setGxjzr(String gxjzr) {
        this.gxjzr = gxjzr == null ? null : gxjzr.trim();
    }

    public String getGxfwq() {
        return gxfwq;
    }

    public void setGxfwq(String gxfwq) {
        this.gxfwq = gxfwq == null ? null : gxfwq.trim();
    }

    public String getGxfwz() {
        return gxfwz;
    }

    public void setGxfwz(String gxfwz) {
        this.gxfwz = gxfwz == null ? null : gxfwz.trim();
    }

    public String getSfygx() {
        return sfygx;
    }

    public void setSfygx(String sfygx) {
        this.sfygx = sfygx == null ? null : sfygx.trim();
    }

    public String getDetailYesorno() {
        return detailYesorno;
    }

    public void setDetailYesorno(String detailYesorno) {
        this.detailYesorno = detailYesorno == null ? null : detailYesorno.trim();
    }

    public String getGxType() {
        return gxType;
    }

    public void setGxType(String gxType) {
        this.gxType = gxType == null ? null : gxType.trim();
    }

    public String getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(String authStatus) {
        this.authStatus = authStatus == null ? null : authStatus.trim();
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
        this.rzlx = rzlx == null ? null : rzlx.trim();
    }

    public String getSfdbts() {
        return sfdbts;
    }

    public void setSfdbts(String sfdbts) {
        this.sfdbts = sfdbts == null ? null : sfdbts.trim();
    }

    public String getQsType() {
        return qsType;
    }

    public void setQsType(String qsType) {
        this.qsType = qsType == null ? null : qsType.trim();
    }

    public String getQsStatus() {
        return qsStatus;
    }

    public void setQsStatus(String qsStatus) {
        this.qsStatus = qsStatus == null ? null : qsStatus.trim();
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode == null ? null : checkCode.trim();
    }

    public String getTxfbz() {
        return txfbz;
    }

    public void setTxfbz(String txfbz) {
        this.txfbz = txfbz == null ? null : txfbz.trim();
    }

    public String getLslbz() {
        return lslbz;
    }

    public void setLslbz(String lslbz) {
        this.lslbz = lslbz == null ? null : lslbz.trim();
    }

    public String getOutStatus() {
        return outStatus;
    }

    public void setOutStatus(String outStatus) {
        this.outStatus = outStatus == null ? null : outStatus.trim();
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
        this.outReason = outReason == null ? null : outReason.trim();
    }

    public Date getOutDate() {
        return outDate;
    }

    public void setOutDate(Date outDate) {
        this.outDate = outDate;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getNewGfTaxno() {
        return newGfTaxno;
    }

    public void setNewGfTaxno(String newGfTaxno) {
        this.newGfTaxno = newGfTaxno;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getJvcode() {
        return jvcode;
    }

    public void setJvcode(String jvcode) {
        this.jvcode = jvcode;
    }

    public String getYxse() {
        return yxse;
    }

    public void setYxse(String yxse) {
        this.yxse = yxse;
    }

    public String getXxly() {
        return xxly;
    }

    public void setXxly(String xxly) {
        this.xxly = xxly;
    }

    public String getGlzt() {
        return glzt;
    }

    public void setGlzt(String glzt) {
        this.glzt = glzt;
    }

    public String getYclx() {
        return yclx;
    }

    public void setYclx(String yclx) {
        this.yclx = yclx;
    }

    public String getYqkgxbz() {
        return yqkgxbz;
    }

    public void setYqkgxbz(String yqkgxbz) {
        this.yqkgxbz = yqkgxbz;
    }
}