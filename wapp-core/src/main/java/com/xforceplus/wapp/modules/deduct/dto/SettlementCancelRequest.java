package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-11-02 11:33
 **/
@Setter
@Getter
public class SettlementCancelRequest {

    @ApiModelProperty("结算单id")
    private Long settlementId;

    @ApiModelProperty("审核类型 0-沃尔玛侧撤销 1-开票金额修改 2-金额有误")
    private Integer type;

    @ApiModelProperty("撤销原因")
    private String revertRemark;
}
