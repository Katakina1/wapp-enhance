package com.xforceplus.wapp.modules.statement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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
    @Length(min = 1, message = "明细ID列表不能为空")
    @ApiModelProperty("明细ID列表")
    private List<Long> ids;
}
