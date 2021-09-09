package com.xforceplus.wapp.modules.cost.entity;


import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class SettlementMatchEntity extends AbstractBaseDomain {

    //费用号
    private String costNo;
    //费用类型
    private String costType;
    //供应商号
    private String venderId;
    //购方税号
    private String gfTaxNo;
    //购方名称
    private String gfName;
    //费用金额
    private BigDecimal costAmount;
    //已冲销金额
    private BigDecimal coveredAmount;
    //未冲销金额
    private BigDecimal uncoveredAmount;
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
    //是否有发票false-没有, true-有
    private String hasInvoice;
    //紧急程度
    private String urgency;
    //BPMS返回的号
    private String epsNo;
    //流程实例id
    private String instanceId;
    //申请单ID
    private String epsId;
    //员工工号
    private String staffNo;

    //成本中心
    private String costDeptIds;

    public String getStaffNo() {
        return staffNo;
    }

    public void setStaffNo(String staffNo) {
        this.staffNo = staffNo;
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

    public String getEpsId() {
        return epsId;
    }

    public void setEpsId(String epsId) {
        this.epsId = epsId;
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

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
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

    public BigDecimal getCostAmount() {
        return costAmount;
    }

    public void setCostAmount(BigDecimal costAmount) {
        this.costAmount = costAmount;
    }

    public BigDecimal getCoveredAmount() {
        return coveredAmount;
    }

    public void setCoveredAmount(BigDecimal coveredAmount) {
        this.coveredAmount = coveredAmount;
    }

    public BigDecimal getUncoveredAmount() {
        return uncoveredAmount;
    }

    public void setUncoveredAmount(BigDecimal uncoveredAmount) {
        this.uncoveredAmount = uncoveredAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }

    public String getCostDeptIds() {
        return costDeptIds;
    }

    public void setCostDeptIds(String costDeptIds) {
        this.costDeptIds = costDeptIds;
    }
}
