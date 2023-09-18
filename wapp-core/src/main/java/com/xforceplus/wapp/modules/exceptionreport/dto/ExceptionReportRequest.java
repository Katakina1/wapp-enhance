package com.xforceplus.wapp.modules.exceptionreport.dto;

import com.xforceplus.wapp.dto.PageViewRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-11 20:49
 **/
@Setter
@Getter
@ApiModel
public class ExceptionReportRequest extends PageViewRequest {
    /**
     * 扣款日期
     */
    @ApiModelProperty("扣款日期-开始 yyyy-MM-dd")
    private String startDeductDate;
    /**
     * 扣款日期purchaserNo
     */
    @ApiModelProperty("扣款日期-结束 yyyy-MM-dd")
    private String endDeductDate;
    /**
     * 供应商号
     */
    @ApiModelProperty("供应商号")
    private String sellerNo;
    /**
     * 供应商名称
     */
    @ApiModelProperty("供应商名称")
    private String sellerName;
    /**
     * 单据号：索赔单号，协议号，EPD号
     */
    @ApiModelProperty("单据号：索赔单号，协议号，EPD号")
    private String billNo;

    @ApiModelProperty("扣款")
    private String purchaserNo;

    @ApiModelProperty("例外报告代码")
    private String code;

    @ApiModelProperty("处理状态,1未处理，2已处理，3无需处理，默认空全部")
    private Integer status;

    /**
     * 报告日期开始时间
     */
    @ApiModelProperty("报告日期-开始 yyyy-MM-dd")
    private String startCreateTime;

    /**
     * 报告日期结束时间
     */
    @ApiModelProperty("报告日期-结束 yyyy-MM-dd")
    private String endCreateTime;



}
