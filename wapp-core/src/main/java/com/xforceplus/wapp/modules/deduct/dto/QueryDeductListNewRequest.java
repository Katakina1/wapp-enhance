package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.List;

/**
 * Describe: 业务单查询请求对象(New 是因为想和之前的参数区分，因为有些参数不需要了，新建的目的是方便后续不熟悉人员看起来清晰点)
 *
 * @Author xiezhongyong
 * @Date 2022/9/8
 */
@ApiModel(description = "查询业务单请求对象")
@Data
public class QueryDeductListNewRequest {


    @ApiModelProperty("是否符合total(优化项，前端可以基于tab接口获取)，默认返回，true=返回total")
    private Boolean totalFalg = Boolean.FALSE;

    @ApiModelProperty("业务单据编号")
    private String businessNo;

    @ApiModelProperty("业务单据类型;1:索赔;2:协议")
    @NotNull(message = "业务单类型不能为空")
    @Pattern(regexp = "^1$|^2$", message = "业务单据类型 不合法")
    private String businessType;

    @ApiModelProperty("供应商编号")
    private String sellerNo;

    @ApiModelProperty("供应商名称")
    private String sellerName;

    @ApiModelProperty("扣款开始日期")
    private String deductStartDate;

    @ApiModelProperty("扣款截至日期")
    private String deductEndDate;

    @ApiModelProperty("扣款公司jv_code")
    private String purchaserNo;

    @ApiModelProperty("页数")
    private Integer pageSize=20;

    @ApiModelProperty("页码")
    private Integer pageNo=1;

    @ApiModelProperty("tab页标签 00：全部；01：待匹配；02：待确认；03：待开票；04：部分开票；05：已开票；06：待审核；07：已取消")
    @Pattern(regexp = "^00$|^01$|^02$|^03$|^04$|^05$|^06$|^07$", message = "tab页标签 不合法")
    private String key;

    @ApiModelProperty("业务单id列表")
    private List<Long> idList;

    @ApiModelProperty("入库日期开始时间，YYYY-MM-DD(协议单使用)")
    private String createTimeBegin;

    @ApiModelProperty("入库日期结束时间，YYYY-MM-DD(协议单使用)")
    private String createTimeEnd;

    @ApiModelProperty("红字信息表编号")
    private String redNotificationNo;

    @ApiModelProperty("结算单号")
    private String refSettlementNo;
    
    @ApiModelProperty("业务单税率")
    private BigDecimal taxRate;

    @ApiModelProperty("红字信息表状态（0：无需申请；1：待申请；2：已申请；3：申请中；4：申请失败；5：撤销中；6：撤销失败；7：已撤销）")
    private List<Integer> redNotificationStatus;

    @ApiModelProperty("列外报告状态（S002：找不到对应税率的蓝票；S004：无索赔明细；S006：只有部分索赔明细；S007：供应商编号错误）")
    private List<String> exceptionReportCodes;

    @ApiModelProperty("业务单明细税率")
    private BigDecimal itemTaxRate;


}
