package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

/**
 * Created by SunShiyong on 2021/10/20.
 */
@ApiModel(description = "查询业务单请求对象")
@Data
public class QueryDeductListRequest {

    /**
     * 业务单据编号
     */
    @ApiModelProperty("业务单据编号")
    private String businessNo;
    /**
     * 业务单据类型;1:索赔;2:协议;3:EPD
     */
    @ApiModelProperty("业务单据类型;1:索赔;2:协议;3:EPD")
    @NotNull(message = "业务单类型不能为空")
    private Integer businessType;
    /**
     * 供应商编号
     */
    @ApiModelProperty("供应商编号")
    private String sellerNo;
    /**
     * 供应商名称
     */
    @ApiModelProperty("供应商名称")
    private String sellerName;
    /**
     * 扣款开始日期
     */
    @ApiModelProperty("扣款开始日期")
    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    private String deductStartDate;
    /**
     * 扣款截至日期
     */
    @ApiModelProperty("扣款截至日期")
    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    private String deductEndDate;
    /**
     * 扣款公司jv_code
     */
    @ApiModelProperty("扣款公司jv_code")
    private String purchaserNo;

    @ApiModelProperty("业务单状态\n" +
            "索赔单:101待匹配明细;102待确认税编;103待确认税差;104待匹配蓝票;105:待匹配结算单;106已匹配结算单;107待审核;108已撤销\n" +
            "协议单:201待匹配结算单;202已匹配结算单;203已锁定;204已取消\n" +
            "EPD单:301待匹配结算单;302已匹配结算单")
    private Integer status;

    @ApiModelProperty("页数")
    private Integer pageSize=99999;

    @ApiModelProperty("页码")
    private Integer pageNo=1;

    @ApiModelProperty("tab页标签 0：待匹配蓝票（业务单）,1：已匹配待开红字信息表（结算单），2：已申请红字信息（结算单），3：已开红票（结算单），4：已撤销（业务单）")
    private String key;

    @ApiModelProperty("id数组")
    private String ids;

    @ApiModelProperty("入库日期开始时间，YYYY-MM-DD")
    private String createTimeBegin;
    @ApiModelProperty("入库日期结束时间，YYYY-MM-DD")
    private String createTimeEnd;
    

    @ApiModelProperty("红字信息表编号")
    private String redNotificationNo;

    @ApiModelProperty("结算单号")
    private String refSettlementNo;
    
    @ApiModelProperty("业务单税率")
    private BigDecimal taxRate;
}
