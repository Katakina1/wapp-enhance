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

    @ApiModelProperty("已撤销")
    private Integer appliedCancel;
    @ApiModelProperty("全部")
    private Integer total;
    @ApiModelProperty("供应商直接删除的数据")
    private Integer sellerDel;

    public SummaryResult(Integer applyPending, Integer applying, Integer applied, Integer waitApprove, Integer total, Integer sellerDel,Integer appliedCancel) {
        this.applyPending = applyPending;
        this.applying = applying;
        this.applied = applied;
        this.waitApprove = waitApprove;
        this.total = total;
        this.sellerDel = sellerDel;
        this.appliedCancel=appliedCancel;
    }
}
