package com.xforceplus.wapp.modules.rednotification.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel("自动红冲请求参数")
@Data
public class AutoApplyRedNotificationRequest {

    @ApiModelProperty(value = "开关状态 on-开 off-关")
    @NotBlank(message = "开关状态不能为空")
    private String switchFlag;

    @ApiModelProperty(value = "开始日期")
    private String startDate;

    @ApiModelProperty(value = "结束日期")
    private String endDate;


}
