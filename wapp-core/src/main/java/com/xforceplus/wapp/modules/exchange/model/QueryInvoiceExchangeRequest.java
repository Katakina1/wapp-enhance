package com.xforceplus.wapp.modules.exchange.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

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
    private String flowType;
    @ApiModelProperty("发票类型")
    private String invoiceType;
    @ApiModelProperty("开票开始日期")
    private String paperDrewStartDate;
    @ApiModelProperty("开票截至日期")
    private String paperDrewEndDate;
    @ApiModelProperty("供应商号")
    private String venderid;
    @ApiModelProperty("换票状态 0初始 1待换票 2已上传 3已完成")
    private Integer exchangeStatus;
    @ApiModelProperty("是否换票 1是  0否")
    private Integer isExchange;
    @ApiModelProperty("发票代码")
    private String invoiceCode;
    @ApiModelProperty("发票号码")
    private String invoiceNo;
    @ApiModelProperty("税率")
    private BigDecimal taxRate;


}
