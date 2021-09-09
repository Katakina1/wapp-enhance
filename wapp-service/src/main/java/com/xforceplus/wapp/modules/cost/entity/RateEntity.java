package com.xforceplus.wapp.modules.cost.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class RateEntity extends AbstractBaseDomain {
    private BigDecimal invoiceAmount;
    private String taxRate;
    private BigDecimal taxAmount;

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public List<CostEntity> getCostTableData() {
        return costTableData;
    }

    public void setCostTableData(List<CostEntity> costTableData) {
        this.costTableData = costTableData;
    }

    private List<CostEntity> costTableData;
    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
