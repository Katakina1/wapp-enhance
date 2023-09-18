package com.xforceplus.wapp.repository.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2022-09-16 11:34
 **/
@Setter
@Getter
@ToString
public class DeductSettlementItemRefDto {
    private Long settlementItemId;
    private Long deductItemId;
    private Long deductId;
    private BigDecimal amountWithTax;
    private BigDecimal amountWithoutTax;
    private BigDecimal taxAmount;

    public BigDecimal getAmountWithoutTax() {
        return getAmountWithTax().subtract(getTaxAmount());
    }

    public BigDecimal getTaxAmount() {
        return Optional.ofNullable(taxAmount).orElse(BigDecimal.ZERO);
    }
}
