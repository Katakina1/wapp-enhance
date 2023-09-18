package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


@Data
@ApiModel
public class SettmentRedListRequest {

    @ApiModelProperty("页码，默认1")
    private int page = 1;

    @ApiModelProperty("每页显示数量")
    private int size = 50;

    @ApiModelProperty("结算单号")
    private String settlementNo;

    @ApiModelProperty("签收状态 0未签收、1已签收")
    private String qsStatus;

    @ApiModelProperty("红字信息编号")
    private String redNotification;

    @ApiModelProperty(value = "供应商代码",hidden = true)
    private String sellerNo;


}
