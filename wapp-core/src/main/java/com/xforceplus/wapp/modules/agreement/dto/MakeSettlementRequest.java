package com.xforceplus.wapp.modules.agreement.dto;

import com.xforceplus.wapp.modules.settlement.dto.PreMakeSettlementRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-21 19:54
 **/
@Setter
@Getter
@ApiModel
public class MakeSettlementRequest extends PreMakeSettlementRequest {
  @ApiModelProperty(value = "是否人工修改 Y-是(默认) N-否")
  private String userModifyFlag;
}
