package com.xforceplus.wapp.modules.statement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;


/**
 * Describe: 结算单 tab 响应
 *
 * @Author xiezhongyong
 * @Date 2022/9/12
 */
@ApiModel(description = "结算单 tab 响应对象")
@Data
@Builder
public class QuerySettlementTabResponse {
    @ApiModelProperty("tab key")
    private String key;
    @ApiModelProperty("数量")
    private Integer count;
    @ApiModelProperty("tab名称")
    private String desc;




}
