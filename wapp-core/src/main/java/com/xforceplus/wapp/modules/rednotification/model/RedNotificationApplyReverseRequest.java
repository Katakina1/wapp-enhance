package com.xforceplus.wapp.modules.rednotification.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel("红字信息表申请撤销请求")
@Data
public class RedNotificationApplyReverseRequest  {
    @ApiModelProperty(value = "红字信息筛选条件")
    private QueryModel queryModel;

    @ApiModelProperty(value = "终端唯一码")
    @NotBlank(message = "终端唯一码不能为空")
    private String terminalUn;

    @ApiModelProperty(value = "设备唯一码")
    @NotBlank(message = "设备唯一码不能为空")
    private String deviceUn;
}
