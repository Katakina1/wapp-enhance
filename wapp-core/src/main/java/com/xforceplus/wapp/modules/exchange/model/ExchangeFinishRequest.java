package com.xforceplus.wapp.modules.exchange.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by SunShiyong on 2021/11/22.
 */
@Data
@ApiModel("换票完成请求")
public class ExchangeFinishRequest {

    @ApiModelProperty("id集合")
    @NotNull(message = "id集合不能为空")
    private List<Long> idList;

    @ApiModelProperty("凭证号")
    @NotBlank(message = "凭证号不能为空")
    private String voucherNo;

}
