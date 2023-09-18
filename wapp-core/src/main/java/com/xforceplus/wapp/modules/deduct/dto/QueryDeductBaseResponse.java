package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Describe: 沃尔玛+供应商 业务单相关共享字段
 *
 * @Author xiezhongyong
 * @Date 2022/9/16
 */
@ApiModel(description = "查询业务单响应对象")
@Data
public class QueryDeductBaseResponse {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("结算单号")
    private String refSettlementNo;
    /**
     * 业务单据编号
     */
    @ApiModelProperty("业务单据编号")
    private String businessNo;
    /**
     * 业务单据类型;1:索赔;2:协议;3:EPD
     */
    @ApiModelProperty("业务单据类型;1:索赔;2:协议;3:EPD")
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
     * 扣款日期
     */
    @ApiModelProperty("扣款日期")
    private Date deductDate;
    /**
     * 扣款公司jv_code
     */
    @ApiModelProperty("扣款公司jv_code")
    private String purchaserNo;

    /**
     * 扣款公司名称
     */
    @ApiModelProperty("扣款公司名称")
    private String purchaserName;

    @ApiModelProperty("协议供应商6D")
    private String agreementMemo;

    @ApiModelProperty("文档类型")
    private String agreementDocumentType;

    @ApiModelProperty("文档编码")
    private String agreementDocumentNumber;

    @ApiModelProperty("协议类型编码")
    private String agreementReasonCode;

    @ApiModelProperty("协议类型")
    private String agreementReference;

    @ApiModelProperty("税码")
    private String agreementTaxCode;

    @ApiModelProperty("含税金额")
    private BigDecimal amountWithTax;

    @ApiModelProperty("不含税金额")
    private BigDecimal amountWithoutTax;

    @ApiModelProperty("税额")
    private BigDecimal taxAmount;

    @ApiModelProperty("税率")
    private BigDecimal taxRate;

    @ApiModelProperty("定案日期")
    private Date verdictDate;

    @ApiModelProperty("批次号")
    private String batchNo;

    @ApiModelProperty("结算单状态")
    private Integer settlementStatus;

    @ApiModelProperty("红字信息表编号")
    private String redNotificationNo;

    @ApiModelProperty("锁定状态 1 锁定 0 未锁定")
    private Integer lockFlag;

    @ApiModelProperty("索赔单:101待匹配明细;102待确认税编;103待确认税差;104待匹配蓝票;105:待匹配结算单;106已匹配结算单;107待审核;108已撤销\n" +
            "协议单:201待匹配结算单;202已匹配结算单;203已锁定;204已取消\n" +
            "EPD单:301待匹配结算单;302已匹配结算单")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("所扣发票")
    private String deductInvoice;

    @ApiModelProperty("明细总不含税金额")
    private BigDecimal itemWithoutAmount;

    @ApiModelProperty("明细总含税金额")
    private BigDecimal itemWithAmount;

    @ApiModelProperty("明细总税额")
    private BigDecimal itemTaxAmount;

    @ApiModelProperty("入库时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("结算单备注")
    private String settlementRemark;

    @ApiModelProperty("列外报告处理状态")
    private Integer exceptionStatus;

    @ApiModelProperty("列外报告")
    private String exceptionCode;

    @ApiModelProperty("列外报告")
    private String exceptionDescription;

    @ApiModelProperty("业务单开票状态(0:未开票;1:部分开票;2:已开票)")
    private Integer makeInvoiceStatus;

    @ApiModelProperty("红字信息表状态（0：无需申请；1：待申请；2：已申请；3：申请中；4：申请失败；5：撤销中；6：撤销失败；7：已撤销）")
    private List<Integer> redNotificationStatus;

    @ApiModelProperty("红字信息表编号列表")
    private List<String> redNotificationNos;

    @ApiModelProperty("历史数据红字编号填充标识")
    private Boolean fullHistoryFlag = Boolean.FALSE;

    /** ps: 前端全部 tab 需要区分 不同的状态展示不同操作*/
    @ApiModelProperty("tab页标签 00：全部；01：待匹配；02：待确认；03：待开票；04：部分开票；05：已开票；06：待审核；07：已取消")
    private QueryTabResp queryTab;


    @Data
    @AllArgsConstructor
    public static class QueryTabResp{
        private String code;
        private String message;
    }

}
