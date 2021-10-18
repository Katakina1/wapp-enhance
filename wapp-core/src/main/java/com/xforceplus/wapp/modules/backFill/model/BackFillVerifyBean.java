package com.xforceplus.wapp.modules.backFill.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Created by SunShiyong on 2021/10/12.
 */


@ApiModel(description = "回填发票对象")
@Data
public class BackFillVerifyBean {

    @JsonProperty("invoiceCode")
    @ApiModelProperty("发票代码")
    private String invoiceCode = null;

    @JsonProperty("invoiceNo")
    @ApiModelProperty("发票号码")
    private String invoiceNo = null;

    @JsonProperty("paperDrewDate")
    @ApiModelProperty("开票日期")
    private String paperDrewDate = null;

    @JsonProperty("checkCode")
    @ApiModelProperty("校验码")
    private String checkCode = null;

    @JsonProperty("amount")
    @ApiModelProperty("不含税金额")
    private String amount = null;

    @JsonProperty("redNoticeNumber")
    @ApiModelProperty("红字信息编码")
    private String redNoticeNumber = null;

    @JsonProperty("invoiceType")
    @ApiModelProperty("发票类型")
    private String invoiceType = null;
}
