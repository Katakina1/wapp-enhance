package com.xforceplus.wapp.enums.exceptionreport;

import lombok.Getter;

/**
 * 非商发票类型
 */
public enum NoneBusinessInvoiceTypeExportEnum {
    BUSINESS_INVOICE_TYPE_SGA("2", "SGA"),
    BUSINESS_INVOICE_TYPE_IC("3", "IC"),
    BUSINESS_INVOICE_TYPE_EC("4", "EC"),
    BUSINESS_INVOICE_TYPE_RE("5", "RE"),
    BUSINESS_INVOICE_TYPE_SR("6", "SR");
    @Getter
    private final String code;
    @Getter
    private final String description;


    NoneBusinessInvoiceTypeExportEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static  String getValue(String code){
        for(NoneBusinessInvoiceTypeExportEnum ele:values()){
            if(ele.getCode().equals(code)){
                return ele.getDescription();
            }
        }
        return null;
    }
    public static  String getValueByDesc(String code){
        for(NoneBusinessInvoiceTypeExportEnum ele:values()){
            if(ele.getDescription().equals(code)){
                return ele.getCode();
            }
        }
        return null;
    }


}
