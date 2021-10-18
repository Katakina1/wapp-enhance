package com.xforceplus.wapp.modules.backFill.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 验真后匹配预制发票请求
 */
@ApiModel(description = "验真后匹配预制发票请求")
@Data
public class BackFillMatchRequest {


    @JsonProperty("settlementNo")
    @ApiModelProperty("结算单号")
    private String settlementNo = null;

    @JsonProperty("invoiceColer")
    @ApiModelProperty("发票颜色  0红票 1蓝票")
    private String invoiceColer = null;

    @JsonProperty("venderId")
    @ApiModelProperty("供应商号")
    private String venderId = null;

    @JsonProperty("verifyBeanList")
    @ApiModelProperty("回填发票列表")
    private List<BackFillVerifyBean> verifyBeanList = new ArrayList<BackFillVerifyBean>();
}
