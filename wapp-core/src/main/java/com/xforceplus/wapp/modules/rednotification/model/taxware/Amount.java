package com.xforceplus.wapp.modules.rednotification.model.taxware;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Amount {
    private BigDecimal amountWithoutTax;

    private BigDecimal amountWithTax;

    private BigDecimal taxAmount;


}
