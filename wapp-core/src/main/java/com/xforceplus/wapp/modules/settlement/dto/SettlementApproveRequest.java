package com.xforceplus.wapp.modules.settlement.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 结算单审核结果
 * @date : 2022/09/27 16:10
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SettlementApproveRequest {

    @ApiModelProperty("结算单id")
    private Long settlementId;

    @ApiModelProperty("结算单号")
    private String settlementNo;

    @ApiModelProperty("操作类型 1-通过 0-驳回")
    private Integer type;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("蓝冲审核的红票列表")
    private List<String> uuids;
}
