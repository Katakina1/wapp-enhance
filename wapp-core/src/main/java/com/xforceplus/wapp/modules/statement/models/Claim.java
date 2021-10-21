package com.xforceplus.wapp.modules.statement.models;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@ApiModel("索赔列表信息")
public class Claim extends BaseInformation {
    @ApiModelProperty("唯一id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @ApiModelProperty("索赔编号")
    private String businessNo;
    @ApiModelProperty("更新时间")
    private Long updateDate;
    @ApiModelProperty("索赔明细")
    private List<ClaimItem> items;
}
