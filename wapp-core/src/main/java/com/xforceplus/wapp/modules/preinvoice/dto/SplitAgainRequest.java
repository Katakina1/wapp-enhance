package com.xforceplus.wapp.modules.preinvoice.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel
@Data
public class SplitAgainRequest {
    @ApiModelProperty("结算单id")
    private Long  settlementId;

    List<PreInvoiceItem> details ;

    /**
     * 结算单类型:1索赔单,2:协议单；3:EPD单
     */
    @ApiModelProperty("结算单类型:1索赔单,2:协议单；3:EPD单")
    private Integer settlementType;

    @ApiModelProperty("操作类型   1 修改税编 2 修改限额 3不做任何修改 4 修改商品明细")
    private Integer applyOperationType;
}
