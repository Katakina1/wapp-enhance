package com.xforceplus.wapp.modules.backfill.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 验真回填提交请求
 */
@ApiModel(description = "验真回填提交请求")
@Data
public class BackFillCommitVerifyRequest {


    @JsonProperty("settlementNo")
    @ApiModelProperty("结算单号")
    private String settlementNo = null;

    @JsonProperty("originInvoiceCode")
    @ApiModelProperty("被蓝冲的发票代码")
    private String originInvoiceCode = null;

    @JsonProperty("originInvoiceNo")
    @ApiModelProperty("被蓝冲的发票号码")
    private String originInvoiceNo = null;

    @JsonProperty("vendorId")
    @ApiModelProperty("供应商ID")
    private String vendorId = null;

    @JsonProperty("jvCode")
    @ApiModelProperty("子公司代码")
    private String jvCode = null;

    @JsonProperty("gfName")
    @ApiModelProperty("购方名称")
    private String gfName = null;

    @JsonProperty("invoiceColor")
    @ApiModelProperty("发票颜色  0红票 1蓝票")
    private String invoiceColor = null;

    @JsonProperty("verifyBeanList")
    @ApiModelProperty("回填发票列表")
    private List<BackFillVerifyBean> verifyBeanList = new ArrayList<BackFillVerifyBean>();
}
