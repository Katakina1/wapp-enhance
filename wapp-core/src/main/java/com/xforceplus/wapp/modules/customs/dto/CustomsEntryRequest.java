package com.xforceplus.wapp.modules.customs.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @Description 入账更新请求
 * @Author pengtao
 * @return
**/
@Data
public class CustomsEntryRequest {

    @ApiModelProperty("02-入账（企业所得税税前扣除）,03-入账（企业所得税不扣除）,06-入账撤销")
    @NotNull(message = "入账标识")
    private String entryStatus;

    @ApiModelProperty("海关缴款书Id")
    @NotNull
    @Size(min = 1, message = "数据不能为空")
    private List<String> ids;

    @ApiModelProperty("是否全选 0 未全选 1全选")
    private String isAllSelected;

    @ApiModelProperty("查询参数，全选时必传")
    private CustomsQueryDto query;
}
