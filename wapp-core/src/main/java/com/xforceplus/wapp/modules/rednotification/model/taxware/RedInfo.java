package com.xforceplus.wapp.modules.rednotification.model.taxware;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RedInfo {

    private String pid;

    private String applyType;

    private String dupTaxFlag;

    private String oilMemo;

    private String purchaserName;

    private String purchaserTaxCode;

    private String sellerName;

    private String sellerTaxCode;

    private String taxCodeVersion;

    private String originalInvoiceType;

    private String originalInvoiceCode;

    private String originalInvoiceNo;

    private String originalInvoiceDate;

    private String applicationReason;

    private Amount amount;

    private List<RedDetailInfo> details;


}
