package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 推荐发票明细列表请求
 **/
@Data
@ApiModel
@Validated
public class InvoiceRecommendDetailListRequest {
    /**
     * 开票时间 开始
     */
    @ApiModelProperty("开票日期--开始")
    @NotNull(message = "需要选择开票日期")
    private String invoiceDateStart;
    /**
     * 开票时间 结束
     */
    @ApiModelProperty("开票日期--结束")
    @NotNull(message = "需要选择开票日期")
    private String invoiceDateEnd;

    @ApiModelProperty("税率")
    @NotNull(message = "税率不能为空")
    private BigDecimal taxRate;

    @ApiModelProperty("税码")
    private String taxCode;

    @NotNull(message = "必须选择一个购方,[purchaserNo]不能为空")
    @ApiModelProperty("购方代码")
    private String purchaserNo;

    @ApiModelProperty(value = "供应商代码",hidden = true)
    private String sellerNo;

    @ApiModelProperty("最新批次号")
    private Integer lastBatchNum;

    @ApiModelProperty("预期返回条数")
    private Integer expectNum;

}
