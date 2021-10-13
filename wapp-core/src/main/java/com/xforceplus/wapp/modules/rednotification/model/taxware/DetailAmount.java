package com.xforceplus.wapp.modules.rednotification.model.taxware;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DetailAmount {
    private BigDecimal amountWithoutTax;

    private BigDecimal quantity;

    private BigDecimal taxAmount;

    private BigDecimal taxDeduction;

    private BigDecimal unitPrice;
}
