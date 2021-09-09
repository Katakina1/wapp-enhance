package com.xforceplus.wapp.modules.report.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 进项税明细中税率对应金额,税额
 */
@Getter
@Setter
public class RateAmountEntity implements Serializable {
    private Double rate;
    private Double amount;
    private Double tax;
}
