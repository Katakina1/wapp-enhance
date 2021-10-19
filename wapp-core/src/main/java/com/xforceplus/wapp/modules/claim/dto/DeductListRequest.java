package com.xforceplus.wapp.modules.claim.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
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
    private String invoiceNo;
    @ApiModelProperty("税率，小数")
    @Max(value = 1,message = "税率仅支持大于0的小数")
    @Min(value = 0,message = "税率仅支持大于0的小数")
    private BigDecimal taxRate;
    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty(hidden = true,value = "销方编号,vendorId,userCode")
    private String sellerNo;
}
