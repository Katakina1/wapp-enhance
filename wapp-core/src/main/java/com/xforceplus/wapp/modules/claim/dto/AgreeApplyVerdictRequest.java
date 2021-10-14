package com.xforceplus.wapp.modules.claim.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
public class AgreeApplyVerdictRequest implements Serializable {

    @ApiModelProperty(value = "结算单id")
    @NotBlank(message = "结算单id不能为空")
    private Long settlementId;

}
