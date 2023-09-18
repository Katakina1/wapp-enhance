package com.xforceplus.wapp.modules.statement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@ApiModel("结算单明细确认")
public class ConfirmDto {
    @NotNull(message = "查询类型不能为空")
    @ApiModelProperty("查询类型 1.索赔、2.协议、3.EPD")
    private Integer statementType;
    @NotBlank(message = "结算单号不能为空")
    @ApiModelProperty("结算单号")
    private String settlementNo;
    @NotBlank(message = "销方编号不能为空")
    @ApiModelProperty("销方编号")
    private String sellerNo;
    @ApiModelProperty("明细ID列表")
    private List<Long> ids;
}
