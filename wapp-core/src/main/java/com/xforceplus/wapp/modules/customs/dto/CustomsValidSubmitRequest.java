package com.xforceplus.wapp.modules.customs.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-09-24 13:57
 **/
@Setter
@Getter
public class CustomsValidSubmitRequest implements Serializable {
    private final static long serialVersionUID = 1L;
    @ApiModelProperty("是否全选 0 未全选 1全选")
    private String isAllSelected;
    @ApiModelProperty("包含项 选中的id")
    private List<Long> includes;

    @ApiModelProperty("排除项 界面的搜索条件")
    private CustomsQueryDto excludes;
}
