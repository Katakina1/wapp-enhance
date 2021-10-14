package com.xforceplus.wapp.modules.rednotification.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("红字信息表信息")
public class RedNotificationInfo {
    @ApiModelProperty("红字信息表主信息")
    RedNotificationMain rednotificationMain;
    @ApiModelProperty("红字信息表明细信息")
    List<RedNotificationItem> redNotificationItemList;
}
