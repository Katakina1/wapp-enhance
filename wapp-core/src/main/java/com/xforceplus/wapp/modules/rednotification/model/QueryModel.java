package com.xforceplus.wapp.modules.rednotification.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@ApiModel
@Data
public class QueryModel {
    @JsonProperty("isAllSelected")
    @ApiModelProperty("是否全选")
    private Boolean isAllSelected = null;

    @JsonProperty("includes")
    @ApiModelProperty("选中的id")
    private List<Long> includes = new ArrayList<Long>();

    @ApiModelProperty("排除id")
    @JsonProperty("excludes")
    private List<Long> excludes = new ArrayList<Long>();

    @ApiModelProperty("申请类型 购方发起:0-已抵扣1-未抵扣 销方发起:2-开票有误")
    private Integer applyType;

    @ApiModelProperty("购方名称")
    private String purchaserName;

    @ApiModelProperty("供应商公司编号")
    private String companyCode;

    @ApiModelProperty("扣款时间毫秒值")
    private Long paymentTime;

    @ApiModelProperty("单号")
    private String billNo;

    @ApiModelProperty("红字信息来源1.索赔单，2协议单，3.EPD")
    private Integer invoiceOrigin;


    @ApiModelProperty("红字信息表编号")
    private String redNotificationNo;

    @ApiModelProperty("分页码 最小1")
    int pageNo ;

    @ApiModelProperty("分页大小")
    int pageSize ;

}
