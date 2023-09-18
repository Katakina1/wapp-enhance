package com.xforceplus.wapp.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 海关票传票清单导出查询对象
 * @Author: ChenHang
 * @Date: 2023/7/18 15:23
 */
@Data
public class CustomsSummonsExportVo implements Serializable {

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
    private CustomsSummonsVo excludes;

}
