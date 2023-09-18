package com.xforceplus.wapp.modules.statement.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * Describe: 结算单列表查询入参
 *
 * @Author xiezhongyong
 * @Date 2022-09-12
 */
@Data
public class QuerySettlementListRequest {

    @ApiModelProperty("业务单据类型;1:索赔;2:协议")
    @NotNull(message = "业务单类型不能为空")
    @Pattern(regexp = "^1$|^2$", message = "业务单据类型 不合法")
    private String businessType;

    @ApiModelProperty("tab页标签 00：全部；01：待确认；02：待开票；03：部分开票；04：已开票；05：已完成；06：待审核；07：已撤销")
    @Pattern(regexp = "^00$|^01$|^02$|^03$|^04$|^05$|^06$|^07$", message = "tab页标签 不合法")
    private String key;

    @ApiModelProperty("结算单状态")
    private Integer settlementStatus;

    @ApiModelProperty("结算单号")
    private String settlementNo;

    @ApiModelProperty("购方编号")
    private String purchaserNo;

    @ApiModelProperty("发票类型（枚举值与结果实体枚举值相同）")
    @Pattern(regexp = "^01$|^03$|^04$|^07$|^08$|^10$|^11$|^14$", message = "发票类型 不合法")
    private String invoiceType;

    @ApiModelProperty("单据号（索赔、协议）")
    private String businessNo;

    @ApiModelProperty("税率")
    private BigDecimal taxRate;

    @ApiModelProperty("页数")
    private Integer pageSize=20;

    @ApiModelProperty("页码")
    private Integer pageNo=1;
}
