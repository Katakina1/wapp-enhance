package com.xforceplus.wapp.modules.taxcode.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class RiversandTcQueryDto {

    @ApiModelProperty("商品号")
    private String itemNo;

    @ApiModelProperty("同步状态")
    private String status;

    @ApiModelProperty("起始新增日期yyyy-mm-dd")
    private String createTimeStart;

    @ApiModelProperty("结束新增日期yyyy-mm-dd")
    private String createTimeEnd;

    @ApiModelProperty("页数")
    private Integer pageSize=20;

    @ApiModelProperty("页码")
    private Integer pageNo=1;

    @ApiModelProperty("是否全选 0 未全选 1全选")
    private String isAllSelected;

}
