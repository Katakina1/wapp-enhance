package com.xforceplus.wapp.modules.InformationInquiry.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.math.BigDecimal;

public class ConfirmInvoiceEntity extends AbstractBaseDomain {

    private String invoiceCode;
    private String invoiceNo;
    private String gfTaxNo;
    private String jvcode;
    private String companyCode;
    private String venderid;
    private String confirmReason;
    private Long confirmUserId;
    private BigDecimal deductibleTaxRate;
    private BigDecimal deductibleTax;

    public BigDecimal getDeductibleTaxRate() {
        return deductibleTaxRate;
    }

    public void setDeductibleTaxRate(BigDecimal deductibleTaxRate) {
        this.deductibleTaxRate = deductibleTaxRate;
    }

    public BigDecimal getDeductibleTax() {
        return deductibleTax;
    }

    public void setDeductibleTax(BigDecimal deductibleTax) {
        this.deductibleTax = deductibleTax;
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

    public String getJvcode() {
        return jvcode;
    }

    public void setJvcode(String jvcode) {
        this.jvcode = jvcode;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }

    public String getConfirmReason() {
        return confirmReason;
    }

    public void setConfirmReason(String confirmReason) {
        this.confirmReason = confirmReason;
    }

    public Long getConfirmUserId() {
        return confirmUserId;
    }

    public void setConfirmUserId(Long confirmUserId) {
        this.confirmUserId = confirmUserId;
    }

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
