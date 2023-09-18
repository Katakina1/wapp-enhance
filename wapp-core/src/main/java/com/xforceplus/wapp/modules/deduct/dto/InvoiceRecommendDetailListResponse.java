package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 推荐发票明细列表回复
 **/
@Setter
@Getter
@ApiModel
public class InvoiceRecommendDetailListResponse {
    @ApiModelProperty("最新批次号")
    private Integer lastBatchNum;
    @ApiModelProperty("预期返回条数")
    private Integer expectNum;
    @ApiModelProperty("实际返回条数")
    private Integer actualNum;
    @ApiModelProperty("发票明细列表")
    private List<MatchedInvoiceDetailBean> detailList = new ArrayList<>();
}
