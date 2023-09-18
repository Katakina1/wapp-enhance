package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel
public class MatchedInvoiceDetailListResponse {
    @ApiModelProperty("合并不含税金额")
    private String mergeAmountWithoutTax;
    @ApiModelProperty("合并含税金额")
    private String mergeAmountWithTax;
    @ApiModelProperty("合并税额")
    private String mergeTaxAmount;
    @ApiModelProperty("目标税率")
    private String targetTaxRate;
    @ApiModelProperty("税码")
    private String taxCode;

    @ApiModelProperty("发票明细列表")
    private List<MatchedInvoiceDetailBean> detailList = new ArrayList<>();
}
