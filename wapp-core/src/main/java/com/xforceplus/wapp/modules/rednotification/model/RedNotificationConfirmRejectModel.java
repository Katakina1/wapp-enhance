package com.xforceplus.wapp.modules.rednotification.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel("确认驳回请求")
@Data
public class RedNotificationConfirmRejectModel {
    @ApiModelProperty(value = "操作类型 confirm：确认,reject:驳回")
    @NotBlank(message = "操作类型不能为空")
    private String operationType;

    @ApiModelProperty(value = "红字信息筛选条件")
    private QueryModel queryModel;

}
