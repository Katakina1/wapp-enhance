package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 推荐发票列表请求
 *
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-18 19:25
 **/
@Data
@ApiModel
@Validated
public class InvoiceRecommendListRequest {

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

    @ApiModelProperty("页码，默认1")
    private int page = 1;

    @ApiModelProperty("每页显示数量")
    private int size = 50;

    @ApiModelProperty("税率")
    @NotNull(message = "税率不能为空")
    private BigDecimal taxRate;

    @NotNull(message = "必须选择一个购方,[purchaserNo]不能为空")
    @ApiModelProperty("购方代码")
    private String purchaserNo;

    @ApiModelProperty(value = "供应商代码",hidden = true)
    private String sellerNo;


}
