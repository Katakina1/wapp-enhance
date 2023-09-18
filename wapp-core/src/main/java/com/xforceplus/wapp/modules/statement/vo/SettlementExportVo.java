package com.xforceplus.wapp.modules.statement.vo;

import com.xforceplus.wapp.modules.statement.dto.QuerySettlementListRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: ChenHang
 * @Date: 2023/8/28 18:53
 */
@Data
public class SettlementExportVo {

    private final static long serialVersionUID = 1L;
    /**
     * 是否全选 0-未全选 1-全选
     */
    @ApiModelProperty("是否全选 0 未全选 1全选")
    private String isAllSelected;
    /**
     * 1 索赔 2 协议 3 EPD
     */
    @ApiModelProperty("1 索赔 2 协议 3 EPD")
    private String businessType;
    /**
     * 包含项
     */
    @ApiModelProperty("包含项 选中的id")
    private List<Long> includes;
    /**
     * 拍出像 界面的搜索条件
     */
    @ApiModelProperty("排除项 界面的搜索条件")
    private QuerySettlementListRequest excludes;

}
