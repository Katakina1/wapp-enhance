package com.xforceplus.wapp.modules.backfill.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by SunShiyong on 2021/10/12.
 */


@ApiModel(description = "回填发票对象")
@Data
public class BackFillVerifyBean {

    @JsonProperty("id")
    @ApiModelProperty("发票ID")
    private Long id = null;

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

    @JsonProperty("machinecode")
    @ApiModelProperty("机器码")
    private String machinecode = null;

    @JsonProperty("redNoticeNumber")
    @ApiModelProperty("红字信息编码")
    private String redNoticeNumber = null;

    @JsonProperty("invoiceType")
    @ApiModelProperty("发票类型")
    private String invoiceType = null;

    @ApiModelProperty("含税金额")
    private String totalAmount;

    @ApiModelProperty("税额")
    private String taxAmount;

    @ApiModelProperty("税率")
    private String taxRate;

    @ApiModelProperty("开票日期")
    private String invoiceDate;

    @JsonProperty("gfName")
    @ApiModelProperty("购方名称")
    private String gfName;

    @JsonProperty("gfTaxNo")
    @ApiModelProperty("购方税号")
    private String gfTaxNo;

    @JsonProperty("xfName")
    @ApiModelProperty("销方名称")
    private String xfName;

    @JsonProperty("xfTaxNo")
    @ApiModelProperty("销方税号")
    private String xfTaxNo;
}
