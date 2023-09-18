package com.xforceplus.wapp.modules.rednotification.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @auther hujintao
 * @date 2022/10/11 9:07
 * @description
 **/
@Data
@NoArgsConstructor
public class RedNotificationRollbackFailResult {

    @ApiModelProperty("红字信息表编号")
    private String redNotificationNo;
    @ApiModelProperty("撤销失败原因")
    private String remark;
}
