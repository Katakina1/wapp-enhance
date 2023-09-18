package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Describe: 供应商 业务单响应
 *
 * @Author xiezhongyong
 * @Date 2022/9/16
 */
@ApiModel(description = "供应商侧-查询业务单响应对象")
@Data
public class QuerySellerDeductListResponse extends QueryDeductBaseResponse{

    /** ps: 前端全部 tab 需要区分 不同的状态展示不同操作*/
    @ApiModelProperty("结算单tab页标签 00：全部；01：待匹配；02：待确认；03：待开票；04：部分开票；05：已开票；06：待审核；07：已取消")
    private QueryTabResp settlementQueryTab;
}
