package com.xforceplus.wapp.modules.exchange.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by SunShiyong on 2021/11/18.
 */
@ApiModel(description = "换票列表请求对象")
@Data
public class QueryInvoiceExchangeRequest {

    @ApiModelProperty("页码")
    private long pageNo = 1;
    @ApiModelProperty("页数")
    private long pageSize = 10;
    @ApiModelProperty("orgcode")
    private String jvcode;
    @ApiModelProperty("业务类型")
    private String businessType;
    @ApiModelProperty("发票类型")
    private String invoiceType;
    @ApiModelProperty("开票开始日期")
    private String paperDrewStartDate;
    @ApiModelProperty("开票截至日期")
    private String paperDrewEndDate;
    @ApiModelProperty("退单号")
    private String returnNo;
    @ApiModelProperty("退单开始日期")
    private String returnStartDate;
    @ApiModelProperty("退单截至日期")
    private String returnEndDate;
    @ApiModelProperty("供应商号")
    private String venderid;

}
