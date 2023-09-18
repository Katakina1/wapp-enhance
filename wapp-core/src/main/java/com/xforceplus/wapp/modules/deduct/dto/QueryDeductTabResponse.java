package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;


/**
 * Describe: 业务单 tab 响应
 *
 * @Author xiezhongyong
 * @Date 2022/9/11
 */
@ApiModel(description = "业务单 tab 响应对象")
@Data
@Builder
public class QueryDeductTabResponse {
    @ApiModelProperty("tab key")
    private String key;
    @ApiModelProperty("数量")
    private Integer count;
    @ApiModelProperty("tab名称")
    private String desc;




}
