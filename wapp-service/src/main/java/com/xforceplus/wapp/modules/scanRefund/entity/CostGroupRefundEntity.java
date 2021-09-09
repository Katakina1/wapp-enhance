package com.xforceplus.wapp.modules.scanRefund.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * 抵账表实体(发票签收)
 */
public class CostGroupRefundEntity implements Serializable {

    //ID
    private Long id;

    //UUID
    private String uuid;

    //UUID
    private String[] uuids;

    //购方名称
    private String gfName;

    //金额
    private BigDecimal invoiceAmount;

    //供应商号
    private String venderId;

    //供应商编码
    private String venderTaxNo;

    //供应商名称
    private String venderName;

    private String createDate;

    private String invoiceDate;

    //税率
    private BigDecimal taxRate;

    //发票类型
    private String invoiceType;

    //发票代码
    private String invoiceCode;

    //发票号码
    private String invoiceNo;

    //购方税号
    private String gfTaxNo;

    //销方税号
    private String xfTaxNo;

    //销方名称
    private String xfName;

    //税额
    private BigDecimal taxAmount;

    //税价合计
    private BigDecimal totalAmount;

    //备注
    private String remark;

    //发票状态
    private String invoiceStatus;

    //状态更新时间
    private String statusUpdateDate;

    //认证日期
    private String rzhDate;

    //签收日期
    private String qsDate;

    //税款所属期
    private String rzhBelongDate;

    //认证状态
    private String rzhYesorno;

    //签收类型
    private String qsType;

    //签收状态
    private String qsStatus;

    //认证结果
    private String authStatus;

    //扫描流水号
    private String invoiceSerialNo;

    private String schemaLabel;

    //是否整组退
    private String refundYesorno;

    //退票理由
    private String refundNotes;

    //费用金额
    private BigDecimal settlementAmount;

    //发票数量
    private Long invoiceNum;

    //匹配日期
    private String matchDate;

    //退单号
    private  String rebateNo;

    //费用号
    private  String costNo;

    //费用类型
    private  String costType;

    //费用金额
    private  String costAmount;

    //未冲销金额
    private  String coveredAmount;

    //费用发生时间
    private  String costTime;

    //用途
    private  String costUse;

    //审批人邮箱
    private  String approverEmail;

    //开户行名称
    private  String bankName;

    //银行账号
    private  String bankAccount;

    //walmart审批状态
    private  String walmartStatus;

    private BigDecimal summationTotalAmount;

    private BigDecimal summationTaxAmount;

    private Long[] ids;

    //匹配失败原因
    private  String reasonForCancel;

    //退票原因
    private  String refundReason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String[] getUuids() {
        return (uuids == null) ? null : Arrays.copyOf(uuids, uuids.length);
    }

    public void setUuids(String[] uuids) {
        this.uuids = uuids == null ? null : Arrays.copyOf(uuids, uuids.length);
    }

    public String getGfName() {
        return gfName;
    }

    public void setGfName(String gfName) {
        this.gfName = gfName;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getVenderTaxNo() {
        return venderTaxNo;
    }

    public void setVenderTaxNo(String venderTaxNo) {
        this.venderTaxNo = venderTaxNo;
    }

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
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

    public String getXfName() {
        return xfName;
    }

    public void setXfName(String xfName) {
        this.xfName = xfName;
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


    public String getRzhYesorno() {
        return rzhYesorno;
    }

    public void setRzhYesorno(String rzhYesorno) {
        this.rzhYesorno = rzhYesorno;
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

    public String getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(String authStatus) {
        this.authStatus = authStatus;
    }

    public String getInvoiceSerialNo() {
        return invoiceSerialNo;
    }

    public void setInvoiceSerialNo(String invoiceSerialNo) {
        this.invoiceSerialNo = invoiceSerialNo;
    }

    public String getSchemaLabel() {
        return schemaLabel;
    }

    public void setSchemaLabel(String schemaLabel) {
        this.schemaLabel = schemaLabel;
    }

    public String getRefundYesorno() {
        return refundYesorno;
    }

    public void setRefundYesorno(String refundYesorno) {
        this.refundYesorno = refundYesorno;
    }

    public String getRefundNotes() {
        return refundNotes;
    }

    public void setRefundNotes(String refundNotes) {
        this.refundNotes = refundNotes;
    }


    public Long getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(Long invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public BigDecimal getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(BigDecimal settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public String getStatusUpdateDate() {
        return statusUpdateDate;
    }

    public void setStatusUpdateDate(String statusUpdateDate) {
        this.statusUpdateDate = statusUpdateDate;
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

    public String getRzhBelongDate() {
        return rzhBelongDate;
    }

    public void setRzhBelongDate(String rzhBelongDate) {
        this.rzhBelongDate = rzhBelongDate;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getRebateNo() {
        return rebateNo;
    }

    public void setRebateNo(String rebateNo) {
        this.rebateNo = rebateNo;
    }

    public String getCostNo() {
        return costNo;
    }

    public void setCostNo(String costNo) {
        this.costNo = costNo;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public String getCostAmount() {
        return costAmount;
    }

    public void setCostAmount(String costAmount) {
        this.costAmount = costAmount;
    }

    public String getCoveredAmount() {
        return coveredAmount;
    }

    public void setCoveredAmount(String coveredAmount) {
        this.coveredAmount = coveredAmount;
    }

    public String getCostTime() {
        return costTime;
    }

    public void setCostTime(String costTime) {
        this.costTime = costTime;
    }

    public String getCostUse() {
        return costUse;
    }

    public void setCostUse(String costUse) {
        this.costUse = costUse;
    }

    public String getApproverEmail() {
        return approverEmail;
    }

    public void setApproverEmail(String approverEmail) {
        this.approverEmail = approverEmail;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getWalmartStatus() {
        return walmartStatus;
    }

    public void setWalmartStatus(String walmartStatus) {
        this.walmartStatus = walmartStatus;
    }

    public BigDecimal getSummationTotalAmount() {
        return summationTotalAmount;
    }

    public void setSummationTotalAmount(BigDecimal summationTotalAmount) {
        this.summationTotalAmount = summationTotalAmount;
    }

    public BigDecimal getSummationTaxAmount() {
        return summationTaxAmount;
    }

    public void setSummationTaxAmount(BigDecimal summationTaxAmount) {
        this.summationTaxAmount = summationTaxAmount;
    }

    public Long[] getIds() {
        return (ids == null) ? null : Arrays.copyOf(ids, ids.length);
    }

    public void setIds(Long[] ids) {
        this.ids = ids == null ? null : Arrays.copyOf(ids, ids.length);
    }

    public String getReasonForCancel() {
        return reasonForCancel;
    }

    public void setReasonForCancel(String reasonForCancel) {
        this.reasonForCancel = reasonForCancel;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }
}
