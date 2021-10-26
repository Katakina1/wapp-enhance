package com.xforceplus.wapp.modules.exceptionreport.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-26 13:39
 **/
@Setter
@Getter
public class ReportExportDto {
    private String code;
    private String description;
    private String sellerNo;
    private String sellerName;
    private String purchaserNo;
    private String amountWithoutTax;
    private String agreementTypeCode;
    private String billNo;
    private String taxCode;
    private String verdictDate;
    private String taxRate;

    public void setTaxRate(String taxRate) {
        if (taxRate != null) {
            final BigDecimal bg = new BigDecimal(taxRate);
            if (bg.compareTo(BigDecimal.ONE) > 0) {
                this.taxRate = bg.setScale(0).toPlainString() + "%";
            } else {
                this.taxRate = bg.movePointRight(2).setScale(0).toPlainString() + "%";
            }
        }
    }
}
