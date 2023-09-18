package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;


@Data
@ApiModel
public class SettmentRedListResponse {

    @ApiModelProperty("结算单号")
    private String settlementNo;

    @ApiModelProperty("签收状态 0未签收、1已签收")
    private String qsStatus;

    @ApiModelProperty("红字信息编号")
    private String redNotification;

}
