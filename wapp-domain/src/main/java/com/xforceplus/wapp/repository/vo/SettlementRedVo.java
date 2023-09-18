package com.xforceplus.wapp.repository.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
public class SettlementRedVo {

    @ApiModelProperty("结算单号")
    private String settlementNo;

    @ApiModelProperty("签收状态 0未签收、1已签收")
    private String qsStatus;

    @ApiModelProperty("红字信息编号")
    private String redNotification;
}
