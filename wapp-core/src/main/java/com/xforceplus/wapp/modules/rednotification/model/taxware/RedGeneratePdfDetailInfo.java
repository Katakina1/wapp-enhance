package com.xforceplus.wapp.modules.rednotification.model.taxware;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedGeneratePdfDetailInfo {

    private String amountWithoutTax;

    private String cargoName;

    private String quantity;

    private String taxAmount;

    private String taxRate;

    private String unitPrice;
}
