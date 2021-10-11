package com.xforceplus.wapp.modules.rednotification.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("统计结果")
@Data
public class SummaryResult {
    @ApiModelProperty("待申请")
    private Integer applyPending;
    @ApiModelProperty("申请中")
    private Integer applying;
    @ApiModelProperty("已申请")
    private Integer applied;
    @ApiModelProperty("待审核")
    private Integer waitApprove;
    @ApiModelProperty("全部")
    private Integer total;
}
