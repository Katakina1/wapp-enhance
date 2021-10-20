package com.xforceplus.wapp.modules.statement.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("结算单tab数量信息")
public class SettlementCount {
    @ApiModelProperty("状态")
    private String status;
    @ApiModelProperty("数量")
    private Integer total;
}