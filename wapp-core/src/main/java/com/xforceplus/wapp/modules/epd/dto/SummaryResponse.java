package com.xforceplus.wapp.modules.epd.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-16 11:41
 **/
@Setter
@Getter
public class SummaryResponse {
    private String taxRateText;
    private int count;
    private double taxRate;
    private boolean isAll;

    public SummaryResponse() {
    }

    public SummaryResponse(int count, String taxRate) {
        this.count = count;
        setTaxRate(taxRate);
    }

    public void setTaxRate(String taxRatestr) {
        BigDecimal taxRate = new BigDecimal(taxRatestr);
        this.taxRate = taxRate.doubleValue();
        if (this.taxRate < 1) {
            final BigDecimal multiply = taxRate.multiply(BigDecimal.valueOf(100));
            final BigDecimal scale = multiply.setScale(0);
            if (scale.compareTo(multiply) == 0) {
                this.taxRateText = scale.toPlainString() + "%税率";
            } else {
                this.taxRateText = multiply.toPlainString() + "%税率";
            }
        }
    }
}
