package com.xforceplus.wapp.enums;

import com.google.common.collect.Maps;

import java.math.BigDecimal;
import java.util.Map;

public enum  TaxRateTransferEnum  {

    TH("17%","TH" ,BigDecimal.valueOf (0.17),BigDecimal.valueOf(17)),
    TM("16%", "TM" ,BigDecimal.valueOf (0.16),  BigDecimal.valueOf(17)),
    TN("13%", "TN" ,BigDecimal.valueOf (0.13),BigDecimal.valueOf(13)),
    TL("11%","TL" ,BigDecimal.valueOf (0.11),BigDecimal.valueOf(11)),
    TO("10%", "TO" ,BigDecimal.valueOf (0.1),BigDecimal.valueOf(10)),
    TP("9%", "TP" ,BigDecimal.valueOf (0.09),BigDecimal.valueOf(9)),
    TG("3%","TG" ,BigDecimal.valueOf (0.03),BigDecimal.valueOf(3)),


    ;
    static {
        init();
    }
    private String orgTaxRate;
    private BigDecimal targetTaxRate;
    private String taxCode;
    private BigDecimal integrityTaxRate;
    public static Map<BigDecimal, BigDecimal> map  ;
    public static void init() {
        map = Maps.newHashMap();
        for (TaxRateTransferEnum  tmp : TaxRateTransferEnum.values()) {
            map.put(tmp.targetTaxRate, tmp.integrityTaxRate);
            map.put(tmp.integrityTaxRate, tmp.targetTaxRate);
        }
    }

    public static BigDecimal transferTaxRate(BigDecimal key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        if (key.compareTo(BigDecimal.ONE) < 0) {
            map.put(key, key.multiply(BigDecimal.valueOf(100)));
            map.put(key.multiply(BigDecimal.valueOf(100)), key);
        }else{
            map.put(key, key.divide(BigDecimal.valueOf(100)));
            map.put(key.divide(BigDecimal.valueOf(100)), key);
        }
        return transferTaxRate(key);
    }
    TaxRateTransferEnum(String orgTaxRate,String taxCode, BigDecimal targetTaxRate,BigDecimal integrityTaxRate) {
        this.orgTaxRate = orgTaxRate;
        this.targetTaxRate = targetTaxRate;
        this.taxCode = taxCode;
        this.integrityTaxRate = integrityTaxRate;
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

//    public static void main(String[] args) {
//        System.out.println(TaxRateTransferEnum.getValue(BigDecimal.valueOf(10)));
//        System.out.println(TaxRateTransferEnum.getValue(BigDecimal.valueOf(0.01)));
//        System.out.println(TaxRateTransferEnum.getValue(BigDecimal.valueOf(11)));
//        System.out.println(TaxRateTransferEnum.getValue(BigDecimal.valueOf(0.11)));
//        System.out.println(TaxRateTransferEnum.getValue(BigDecimal.valueOf(0.3)));
//    }
}