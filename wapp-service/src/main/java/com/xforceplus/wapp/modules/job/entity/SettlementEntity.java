package com.xforceplus.wapp.modules.job.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementFileEntity;
import lombok.Getter;
import lombok.Setter;

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
    //结算金额
    private String settlementAmount;
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
    //合同
    private String contract;
    //是否有发票0-没有, 1-有
    private String hasInvoice;
    //紧急程度
    private String urgency;
    //扫描匹配状态
    private String scanStatus;
    //流程实例id
    private String instanceId;
    //0-非预付款  1-预付款  2 --BPMS 获取带票付款
    private String payModel;



    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
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

    public String getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(String settlementAmount) {
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

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getPayModel() {
        return payModel;
    }

    public void setPayModel(String payModel) {
        this.payModel = payModel;
    }
}
