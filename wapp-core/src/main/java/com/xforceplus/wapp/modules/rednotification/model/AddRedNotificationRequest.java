package com.xforceplus.wapp.modules.rednotification.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("同步预制发票信息到红字信息模块")
@Data
public class AddRedNotificationRequest {
    List<RedNotificationInfo> redNotificationInfoList;
    @ApiModelProperty("0 不自动申请 1自动申请")
    int autoApplyFlag ;
}
