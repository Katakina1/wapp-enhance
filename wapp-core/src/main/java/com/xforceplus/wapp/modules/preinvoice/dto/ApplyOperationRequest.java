package com.xforceplus.wapp.modules.preinvoice.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ApplyOperationRequest {

    @ApiModelProperty("结算单号")
    private String settlementNo;

    @ApiModelProperty("结算单id")
    private String settlementId;

    @ApiModelProperty("操作类型   1 修改税编 2 修改限额 3不做任何修改 4 修改商品明细")
    private int applyOperationType;

    /**
     * 结算单类型:1索赔单,2:协议单；3:EPD单
     */
    @ApiModelProperty("结算单类型:1索赔单,2:协议单；3:EPD单")
    private Integer settlementType;

}
