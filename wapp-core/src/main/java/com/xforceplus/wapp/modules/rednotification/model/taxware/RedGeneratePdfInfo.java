package com.xforceplus.wapp.modules.rednotification.model.taxware;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RedGeneratePdfInfo {

    private Integer applicant;

    private String date;

    private String originInvoiceCode;

    private String originInvoiceNo;

    private String purchaseTaxNo;

    private String purchaserName;

    private String redNotificationNo;

    private String sellerName;

    private String sellerTaxNo;

    private String totalAmountWithoutTax;

    private String totalTaxAmount;

    private List<RedGeneratePdfDetailInfo> details;
}
