package com.xforceplus.wapp.modules.customs.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName QueryCustomsTabResponse
 * @Description TODO
 * @Date 2023/6/20 15:49
 * @Author pengtao
 * @Version V1.0
 **/
@ApiModel(description = "海关缴款书 tab 响应对象")
@Data
@Builder
public class QueryCustomsTabResponse {
    @ApiModelProperty("页签类型")
    private String key;
    @ApiModelProperty("数量")
    private Integer num;
    @ApiModelProperty("tab名称")
    private String desc;
}
