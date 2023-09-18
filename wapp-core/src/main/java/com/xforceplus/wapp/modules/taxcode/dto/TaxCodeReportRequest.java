package com.xforceplus.wapp.modules.taxcode.dto;

import com.xforceplus.wapp.dto.PageViewRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-11 20:49
 **/
@Setter
@Getter
@ApiModel
public class TaxCodeReportRequest extends PageViewRequest {
    @ApiModelProperty("选中的id")
    private List<Long> ids;

    /**
     * 商品编号
     */
    @ApiModelProperty("商品编号")
    private String itemNo;
    /**
     * 商品名称
     */
    @ApiModelProperty("商品名称")
    private String itemName;

    @ApiModelProperty("处理状态 0未处理 1已处理")
    private String disposeStatus;

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
    /**
     * 报告日期开始时间
     */
    @ApiModelProperty("报告更新-开始 yyyy-MM-dd")
    private String startUpdateTime;

    /**
     * 报告日期结束时间
     */
    @ApiModelProperty("报告更新-结束 yyyy-MM-dd")
    private String endUpdateTime;



}
