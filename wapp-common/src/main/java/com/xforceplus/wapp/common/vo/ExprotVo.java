package com.xforceplus.wapp.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: ChenHang
 * @Date: 2023/8/9 18:03
 */
@Data
public class ExprotVo<T> {

    private final static long serialVersionUID = 1L;
    /**
     * 是否全选 0-未全选 1-全选
     */
    @ApiModelProperty("是否全选 0 未全选 1全选")
    private String isAllSelected;
    /**
     * 包含项
     */
    @ApiModelProperty("包含项 选中的id")
    private List<Long> includes;
    /**
     * 拍出像 界面的搜索条件
     */
    @ApiModelProperty("排除项 界面的搜索条件")
    private T excludes;

}
