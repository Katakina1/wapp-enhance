package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * Created by SunShiyong on 2021/10/20.
 */
@ApiModel(description = "沃尔玛侧-查询业务单响应对象")
@Data
public class QueryDeductListResponse extends QueryDeductBaseResponse{

    @ApiModelProperty("发票类型")
    private String invoiceType;
}
