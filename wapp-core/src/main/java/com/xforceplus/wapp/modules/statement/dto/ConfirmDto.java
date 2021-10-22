package com.xforceplus.wapp.modules.statement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@ApiModel("结算单明细确认")
public class ConfirmDto {
    @ApiModelProperty("结算单号")
    private String settlementNo;
    @ApiModelProperty("销方编号")
    private String sellerNo;
    @ApiModelProperty("明细ID列表")
    private List<Long> ids;
}
