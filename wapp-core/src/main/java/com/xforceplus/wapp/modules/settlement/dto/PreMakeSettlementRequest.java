package com.xforceplus.wapp.modules.settlement.dto;

import com.xforceplus.wapp.modules.deduct.model.DeductInvoiceDetailData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-30 20:27
 **/
@Setter
@Getter
@ApiModel
public class PreMakeSettlementRequest {
    @ApiModelProperty(value = "购方编码/机构代码")
    private String purchaserNo;

    @ApiModelProperty
    private BigDecimal taxRate;
    @ApiModelProperty
    private List<Long> billIds;
    @ApiModelProperty(value = "匹配指定蓝票ID列表")
    private List<Long> invoiceIds;
    @ApiModelProperty(value = "匹配指定蓝票明细列表")
    private List<DeductInvoiceDetailData> detailDataList;

    @ApiModelProperty(hidden = true)
    private String sellerNo;
}
