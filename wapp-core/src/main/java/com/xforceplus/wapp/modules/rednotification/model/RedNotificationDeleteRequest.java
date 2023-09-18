package com.xforceplus.wapp.modules.rednotification.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("删除待申请的红字信息表")
@Data
public class RedNotificationDeleteRequest {
	@ApiModelProperty(value = "红字信息筛选条件")
	private QueryModel queryModel;

	@ApiModelProperty(value = "红字信息表ID")
	private String[] redId;
}
