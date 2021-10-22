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
    @NotBlank(message = "结算单号不能为空")
    @ApiModelProperty("结算单号")
    private String settlementNo;
    @NotBlank(message = "销方编号不能为空")
    @ApiModelProperty("销方编号")
    private String sellerNo;
    @NotNull
    @Size(min = 1, message = "明细ID列表不能为空")
    @ApiModelProperty("明细ID列表")
    private List<Long> ids;
}
