package com.xforceplus.wapp.modules.claim.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class SettlementApplyVerdictRequest extends ApplyVerdictRequest {

    @ApiModelProperty(value = "结算单id")
    @NotBlank(message = "结算单id不能为空")
    private Long settlementId;

}
