package com.xforceplus.wapp.modules.exchange.model;

import com.xforceplus.wapp.modules.backfill.model.BackFillVerifyBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 验真后保存换的新票请求
 */
@ApiModel(description = "验真后保存换的新票请求")
@Data
public class BackFillExchangeRequest {


    @NotNull(message = "发票ID不能为空")
    @ApiModelProperty("需要换票的发票id")
    private Long invoiceId = null;

    @ApiModelProperty("供应商号")
    private String venderId = null;

    @ApiModelProperty("回填发票id")
    private List<BackFillVerifyBean> verifyBeanList = new ArrayList<BackFillVerifyBean>();


    @ApiModelProperty("换票原因")
    private String exchangeReason = null;

}
