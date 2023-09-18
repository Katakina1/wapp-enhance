package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.List;

/**
 * Describe: 供应商业务单参数入参
 *
 * @Author xiezhongyong
 * @Date 2022/9/16
 */
@ApiModel(description = "供应商查询业务单请求对象")
@Data
public class QuerySellerDeductListRequest {



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

    @ApiModelProperty("扣款日期-开始(YYYY-MM-DD)")
    private String deductDateStart;

    @ApiModelProperty("扣款日期-结束(YYYY-MM-DD)")
    private String deductDateEnd;

    @ApiModelProperty("扣款发票号码")
    private String deductInvoice;

    @ApiModelProperty("扣款公司jv_code")
    private String purchaserNo;

    @ApiModelProperty("页数")
    private Integer pageSize=20;

    @ApiModelProperty("页码")
    private Integer pageNo=1;

    @ApiModelProperty("业务单tab状态 00：全部；01：待匹配；02：待确认；03：待开票；04：部分开票；05：已开票；06：待审核；07：已取消")
    @Pattern(regexp = "^00$|^01$|^02$|^03$|^04$|^05$|^06$|^07$", message = "tab页标签 不合法")
    private String businessTabKey;

    @ApiModelProperty("业务单id列表")
    private List<Long> idList;

    @ApiModelProperty("入库日期-开始(YYYY-MM-DD)")
    private String createTimeStart;

    @ApiModelProperty("入库日期-结束(YYYY-MM-DD)")
    private String createTimeEnd;

    @ApiModelProperty("定案日期-开始(YYYY-MM-DD)")
    private String verdictDateStart;

    @ApiModelProperty("定案日期-结束(YYYY-MM-DD)")
    private String verdictDateEnd;

    @ApiModelProperty("红字信息表编号")
    private String redNotificationNo;

    @ApiModelProperty("结算单号")
    private String refSettlementNo;

    @ApiModelProperty("结算单tab状态 00：全部；01：待确认；02：待开票；03：部分开票；04：已开票；05：已完成；06：待审核；07：已撤销")
    @Pattern(regexp = "^00$|^01$|^02$|^03$|^04$|^05$|^06$|^07$", message = "tab页标签 不合法")
    private String settlementTabKey;
    
    @ApiModelProperty("业务单税率")
    private BigDecimal taxRate;

    @ApiModelProperty("红字信息表状态（0：无需申请；1：待申请；2：已申请；3：申请中；4：申请失败；5：撤销中；6：撤销失败；7：已撤销）")
    private List<Integer> redNotificationStatus;

    @ApiModelProperty("列外报告状态（S002：找不到对应税率的蓝票；S004：无索赔明细；S006：只有部分索赔明细）")
    private List<String> exceptionReportCodes;




}
