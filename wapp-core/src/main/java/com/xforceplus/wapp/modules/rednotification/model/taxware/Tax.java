package com.xforceplus.wapp.modules.rednotification.model.taxware;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Tax {
    /**
     * 是否享受税收优惠政策，默认值 false
     */
    private boolean preferentialTax;
    /**
     * 享受税收优惠政策内容
     */
    private String taxPolicy;
    /**
     * 税率(例如，16%传0.16)
     */
    private BigDecimal taxRate;
    /**
     * 税率标志 (空-非0税率；0-出口退税 1-免税 2-不征税 3-普通0税率)
     */
    private String zeroTax;

    /**
     * 税编版本（例如：32.0）
     */
    private String taxCodeVersion;


}
