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

    /**
     * 含税金额
     */
    @ApiModelProperty("含税金额")
    private BigDecimal amountWithTax;
    /**
     * 不含税金额
     */
    @ApiModelProperty("不含税金额")
    private BigDecimal amountWithoutTax;

    /**
     * 税额
     */
    @ApiModelProperty("税额")
    private BigDecimal taxAmount;
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


}
