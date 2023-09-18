package com.xforceplus.wapp.enums.exceptionreport;

import lombok.Getter;

/**
 * 非商发票类型
 */
public enum NoneBusinessInvoiceTypeExportEnum {
    BUSINESS_INVOICE_TYPE_SGA("1", "SG&A"),
    BUSINESS_INVOICE_TYPE_IC("5", "IC"),
    BUSINESS_INVOICE_TYPE_RE("3", "RE"),
    USINESS_INVOICE_TYPE_EC("6", "EC"),
    USINESS_INVOICE_TYPE_RE("7", "BR"),
    USINESS_INVOICE_TYPE_SR("8", "SR"),
    BUSINESS_INVOICE_TYPE_SR("4", "FA"),
    BUSINESS_INVOICE_TYPE_FAEP("9", "FA&EP"),
    BUSINESS_INVOICE_TYPE_CONCUR("10", "Concur");
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
