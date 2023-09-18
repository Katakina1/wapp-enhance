package com.xforceplus.wapp.modules.customs.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


@Data
public class CustomsCheckRequest {

    @ApiModelProperty("认证所属期yyyyMM")
    @NotNull(message = "认证所属期不能为空")
    private String taxPeriod;


    @ApiModelProperty("1-抵扣勾选 10-撤销抵扣勾选  3-退税勾选 30-退税撤销勾选（确认后无法撤销）")
    @NotNull(message = "认证用途不能为空")
    private String authUse;


    @ApiModelProperty("海关缴款书Id")
    @NotNull
    @Size(min = 1, message = "数据不能为空")
    private List<String> ids;

    @ApiModelProperty("是否全选 0 未全选 1全选")
    private String isAllSelected;

    @ApiModelProperty("查询参数，全选时必传")
    private CustomsQueryDto query;
}
