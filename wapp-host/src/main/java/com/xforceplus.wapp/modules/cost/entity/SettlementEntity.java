package com.xforceplus.wapp.modules.cost.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.math.BigDecimal;
import java.util.List;

public class SettlementEntity extends AbstractBaseDomain {

    //供应商号
    private String venderId;
    //供应商名称
    private String venderName;
    //开户行名称
    private String bankName;
    //银行账号
    private String bankAccount;
    //审批人邮箱
    private String approverEmail;
    //审批人工号
    private String staffNo;
    //结算金额
    private BigDecimal settlementAmount;
    //沃尔玛审核状态
    private String walmartStatus;
    //费用号
    private String costNo;
    //创建日期
    private String createDate;
    //备注
    private String remark;
    //属于
    private String belongsTo;
    //业务类型
    private String serviceType;
    //付款方式
    private String paymentMode;
    //付款基准日
    private String payDay;
    //合同类型 0-非合同 1-合同
    private String businessType;
    //合同
    private String contract;
    //是否有发票false-没有, true-有
    private String hasInvoice;
    //紧急程度
    private String urgency;
    //扫描状态
    private String scanStatus;
    //0-非预付款  1-预付款
    private String payModel;
    //BPMS返回的号
    private String epsNo;
    //流程实例id
    private String instanceId;
    //申请单ID
    private String epsId;
    //原BINDID, 发票补录用
    private String oldBindId;
    //原申请单金额, 发票补录用
    private BigDecimal oldTotalAmount;
    //剩余金额, 发票补录用
    private BigDecimal surplusAmount;
    //登录名
    private String loginName;

    public String getStaffNo() {
        return staffNo;
    }

    public void setStaffNo(String staffNo) {
        this.staffNo = staffNo;
    }

    public BigDecimal getOldTotalAmount() {
        return oldTotalAmount;
    }

    public void setOldTotalAmount(BigDecimal oldTotalAmount) {
        this.oldTotalAmount = oldTotalAmount;
    }

    public String getOldBindId() {
        return oldBindId;
    }

    public void setOldBindId(String oldBindId) {
        this.oldBindId = oldBindId;
    }

    public BigDecimal getSurplusAmount() {
        return surplusAmount;
    }

    public void setSurplusAmount(BigDecimal surplusAmount) {
        this.surplusAmount = surplusAmount;
    }

    public String getEpsId() {
        return epsId;
    }

    public void setEpsId(String epsId) {
        this.epsId = epsId;
    }

    //发票列表
    private List<RecordInvoiceEntity> invoiceList;
    //文件列表
    private List<SettlementFileEntity> fileList;

    //发票税率列表,推送BPMS用
    private List<InvoiceRateEntity> invoiceRateList;

    public String getEpsNo() {
        return epsNo;
    }

    public void setEpsNo(String epsNo) {
        this.epsNo = epsNo;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
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

    public String getApproverEmail() {
        return approverEmail;
    }

    public void setApproverEmail(String approverEmail) {
        this.approverEmail = approverEmail;
    }

    public BigDecimal getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(BigDecimal settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public String getWalmartStatus() {
        return walmartStatus;
    }

    public void setWalmartStatus(String walmartStatus) {
        this.walmartStatus = walmartStatus;
    }

    public String getCostNo() {
        return costNo;
    }

    public void setCostNo(String costNo) {
        this.costNo = costNo;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBelongsTo() {
        return belongsTo;
    }

    public void setBelongsTo(String belongsTo) {
        this.belongsTo = belongsTo;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPayDay() {
        return payDay;
    }

    public void setPayDay(String payDay) {
        this.payDay = payDay;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getHasInvoice() {
        return hasInvoice;
    }

    public void setHasInvoice(String hasInvoice) {
        this.hasInvoice = hasInvoice;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getScanStatus() {
        return scanStatus;
    }

    public void setScanStatus(String scanStatus) {
        this.scanStatus = scanStatus;
    }

    public String getPayModel() {
        return payModel;
    }

    public void setPayModel(String payModel) {
        this.payModel = payModel;
    }

    public List<RecordInvoiceEntity> getInvoiceList() {
        return invoiceList;
    }

    public void setInvoiceList(List<RecordInvoiceEntity> invoiceList) {
        this.invoiceList = invoiceList;
    }

    public List<SettlementFileEntity> getFileList() {
        return fileList;
    }

    public void setFileList(List<SettlementFileEntity> fileList) {
        this.fileList = fileList;
    }

    public List<InvoiceRateEntity> getInvoiceRateList() {
        return invoiceRateList;
    }

    public void setInvoiceRateList(List<InvoiceRateEntity> invoiceRateList) {
        this.invoiceRateList = invoiceRateList;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
