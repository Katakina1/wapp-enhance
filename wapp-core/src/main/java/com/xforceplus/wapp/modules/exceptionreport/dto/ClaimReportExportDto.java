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
public class ClaimReportExportDto {
    private String code;
    private String description;
    private String sellerNo;
    private String sellerName;
    private String purchaserName;
    private String amountWithoutTax;
    private String billNo;
    private String verdictDate;
    private String deductDate;

}
