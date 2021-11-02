package com.xforceplus.wapp.modules.claim.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-15 16:44
 **/
@Setter
@Getter
@Valid
public class DeductListRequest {
    @ApiModelProperty("扣款公司代码")
    @NotNull(message = "必须要选择一个扣款公司")
    private String purchaserNo;
    @ApiModelProperty("索赔号/协议号/EPD号")
    private String billNo;
    /**
     * 页码
     */
    @ApiModelProperty("页码，默认1")
    private long page = 1;
    @ApiModelProperty("每页显示条数")
    private long size = 50;
    @ApiModelProperty("扣款日期开始时间，YYYY-MM-DD")
    private String deductDateBegin;
    @ApiModelProperty("扣款日期结束时间，YYYY-MM-DD")
    private String deductDateEnd;
    @ApiModelProperty("定案日期开始时间，YYYY-MM-DD")
    private String verdictDateBegin;
    @ApiModelProperty("定案日期结束时间，YYYY-MM-DD")
    private String verdictDateEnd;
    @ApiModelProperty("扣款发票号码，YYYY-MM-DD")
    private String deductInvoice;
    @ApiModelProperty("税率，小数")
    private BigDecimal taxRate;
    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("是否超期，1超期，0未超期")
    private Integer overdue;

    @ApiModelProperty(hidden = true,value = "销方编号,vendorId,userCode")
    private String sellerNo;

    @ApiModelProperty(hidden = true)
    private Integer lockFlag;
}
