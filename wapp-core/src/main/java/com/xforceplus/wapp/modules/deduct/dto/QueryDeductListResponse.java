package com.xforceplus.wapp.modules.deduct.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by SunShiyong on 2021/10/20.
 */
@ApiModel(description = "查询业务单响应对象")
@Data
public class QueryDeductListResponse {
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

    @ApiModelProperty("协议供应商6D")
    private String agreementMemo;

    @ApiModelProperty("协议类型")
    private String agreementDocumentType;

    @ApiModelProperty("协议类型编码")
    private String agreementReasonCode;

    @ApiModelProperty("税码")
    private String agreementTaxCode;

    /**
     * 含税金额
     */
    @ApiModelProperty("含税金额")
    private BigDecimal amountWithTax;
    /**
     * 税率
     */
    @ApiModelProperty("税率")
    private BigDecimal taxRate;

    @ApiModelProperty("定案日期")
    private Date verdictDate;

    @ApiModelProperty("批次号")
    private String batchNo;

    @ApiModelProperty("发票类型")
    private String invoiceType;

    @ApiModelProperty("红字信息表编号")
    private String redNotificationNo;

}
