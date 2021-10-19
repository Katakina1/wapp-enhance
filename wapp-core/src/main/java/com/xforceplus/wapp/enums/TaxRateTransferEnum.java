package com.xforceplus.wapp.enums;

import java.math.BigDecimal;

public enum  TaxRateTransferEnum  {
    TH("17%","TH" ,new BigDecimal("0.17")),
    TM("16%", "TM" ,new BigDecimal("0.16")),
    TN("13%", "TN" ,new BigDecimal("0.13")),
    TL("11%","TL" ,new BigDecimal("0.11")),
    TO("10%", "TO" ,new BigDecimal("0.1")),
    TP("9%", "TP" ,new BigDecimal("0.09")),
    TG("3%","TG" ,new BigDecimal("0.03")),


    ;
    private String orgTaxRate;
    private String taxCode;
    private BigDecimal targetTaxRate;

    TaxRateTransferEnum(String orgTaxRate,String taxCode, BigDecimal targetTaxRate) {
        this.orgTaxRate = orgTaxRate;
        this.targetTaxRate = targetTaxRate;
        this.taxCode = taxCode;
    }

    public String getOrgTaxRate() {
        return orgTaxRate;
    }

    public void setOrgTaxRate(String orgTaxRate) {
        this.orgTaxRate = orgTaxRate;
    }

    public BigDecimal getTargetTaxRate() {
        return targetTaxRate;
    }

}