package com.xforceplus.wapp.modules.customs.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CustomsAmountDto {

    @ApiModelProperty("税额合计")
    private BigDecimal taxAmountTotal;

    @ApiModelProperty("有效抵扣税额合计")
    private BigDecimal effTaxAmountTotal;

    @ApiModelProperty("全选数量")
    private int count;
}
