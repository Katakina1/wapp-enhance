package com.xforceplus.wapp.modules.taxcode.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class RiversandTcValidSubmitRequest implements Serializable {
    private final static long serialVersionUID = 1L;
    @ApiModelProperty("是否全选 0 未全选 1全选")
    private String isAllSelected;
    @ApiModelProperty("包含项 选中的id")
    private List<Long> includes;

    @ApiModelProperty("排除项 界面的搜索条件")
    private RiversandTcQueryDto excludes;
}
