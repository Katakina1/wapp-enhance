package com.xforceplus.wapp.modules.statement.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("结算单确认索赔列表信息")
public class ClaimConfirm extends BaseConfirm {
    @ApiModelProperty("索赔编号")
    private String businessNo;
    @ApiModelProperty("更新时间")
    private List<ConfirmItem> items;
}
