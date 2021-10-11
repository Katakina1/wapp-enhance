package com.xforceplus.wapp.modules.rednotification.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@ApiModel("获取终端结果")
public class GetTerminalResult {
    @ApiModelProperty("公司名称")
    @JsonProperty("newCompanyName")
    private String newCompanyName;
    @ApiModelProperty("发票类型")
    @JsonProperty("invoiceType")
    private String invoiceType;
    @ApiModelProperty("申请数量")
    @JsonProperty("invoiceCount")
    private Integer invoiceCount;

    @ApiModelProperty("含税金额")
    @JsonProperty("amountWithTax")
    private String amountWithTax;
    @ApiModelProperty("不含税金额")
    @JsonProperty("amountWithoutTax")
    private String amountWithoutTax;
    @ApiModelProperty("税额")
    @JsonProperty("taxAmount")
    private String taxAmount;
    @ApiModelProperty("终端列表")
    @JsonProperty("terminalList")
    private List<TerminalDTO> terminalList;


}
