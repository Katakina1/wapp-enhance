package com.xforceplus.wapp.modules.cost.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.math.BigDecimal;

public class CostEntity extends AbstractBaseDomain {
    private String costType;
    private String costTypeName;
    private String costTime;
    private BigDecimal costAmount;
    private String costUse;
    private BigDecimal coveredAmount;
    private BigDecimal uncoveredAmount;
    private String venderId;
    private String costDept;
    private String costDeptId;
    private String projectCode;
    private String instanceId;
    private String bpmsId;
    //原BINDID, 发票补录用
    private String oldBindId;
    //剩余金额, 发票补录用
    private BigDecimal surplusAmount;
    //(0-合同 1-非合同)
    private String isContract;

    public String getIsContract() {
        return isContract;
    }

    public void setIsContract(String isContract) {
        this.isContract = isContract;
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

    public String getCostDeptId() {
        return costDeptId;
    }

    public void setCostDeptId(String costDeptId) {
        this.costDeptId = costDeptId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getBpmsId() {
        return bpmsId;
    }

    public void setBpmsId(String bpmsId) {
        this.bpmsId = bpmsId;
    }

    public String getCostDept() {
        return costDept;
    }

    public void setCostDept(String costDept) {
        this.costDept = costDept;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public String getCostTypeName() {
        return costTypeName;
    }

    public void setCostTypeName(String costTypeName) {
        this.costTypeName = costTypeName;
    }

    public String getCostTime() {
        return costTime;
    }

    public void setCostTime(String costTime) {
        this.costTime = costTime;
    }

    public BigDecimal getCostAmount() {
        return costAmount;
    }

    public void setCostAmount(BigDecimal costAmount) {
        this.costAmount = costAmount;
    }

    public String getCostUse() {
        return costUse;
    }

    public void setCostUse(String costUse) {
        this.costUse = costUse;
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

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
