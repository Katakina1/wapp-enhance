package com.xforceplus.wapp.modules.agreement.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 协议结算单操作流程步骤
 * @date : 2022/10/13 14:39
 **/
@Data
@NoArgsConstructor
public class AgreementOperateStepRequest {

    @NotBlank
    @ApiModelProperty("结算单号")
    private String settlementNo;

    @Max(value = 3, message = "该步骤不支持操作")
    @Min(value = 2, message = "该步骤不支持操作")
    @ApiModelProperty("所处步骤 2-修改税编 3-开票预览")
    private Integer step;

    @Max(value = 2, message = "未知操作类型")
    @Min(value = 0, message = "未知操作类型")
    @ApiModelProperty("操作 0-取消 1-上一步 2-下一步")
    private Integer type;
}
