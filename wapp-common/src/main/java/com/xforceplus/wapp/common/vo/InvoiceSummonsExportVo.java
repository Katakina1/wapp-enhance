package com.xforceplus.wapp.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 导出条件
 * @Author: ChenHang
 * @Date: 2023/8/9 18:01
 */
@Data
public class InvoiceSummonsExportVo implements Serializable {

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
    private InvoiceSummonsVo excludes;

}
