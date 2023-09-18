package com.xforceplus.wapp.modules.exchange.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by SunShiyong on 2021/11/18.
 */
@ApiModel(description = "换票对象")
@Data
public class InvoiceExchangeResponse {

    @ApiModelProperty("底账发票id")
    private Long invoiceId;

    /**
     * 状态 0待换票 1已上传 2已完成 9删除
     */
    @ApiModelProperty("换票状态 0初始 1待换票 2已上传 3已完成")
    private Integer exchangeStatus;

    /**
     * 退单日期
     */
    @ApiModelProperty("退单日期")
    private Date rebateDate;

    /**
     * orgcode
     */
    @ApiModelProperty("orgcode")
    private String jvcode;

    /**
     * 供应商编码
     */
    @ApiModelProperty("供应商编码")
    private String sellerNo;

    /**
     * 不含税金额
     */
    @ApiModelProperty("不含税金额")
    private BigDecimal amountWithoutTax;

    /**
     * 不含税金额
     */
    @ApiModelProperty("不含税金额")
    private BigDecimal amountWithTax;

    /**
     * 税额
     */
    @ApiModelProperty("税额")
    private BigDecimal taxAmount;

    @ApiModelProperty("税率")
    private BigDecimal taxRate;

    /**
     * 业务类型
     */
    @ApiModelProperty("业务类型")
    private String flowType;

    /**
     * 发票代码
     */
    @ApiModelProperty("发票代码")
    private String invoiceCode;

    /**
     * 发票号码
     */
    @ApiModelProperty("发票号码")
    private String invoiceNo;

    /**
     * 开票日期
     */
    @ApiModelProperty("开票日期")
    private String paperDrewDate;

    @ApiModelProperty("换票原因")
    private String exchangeReason;

    @ApiModelProperty("发票类型")
    private String invoiceType;

    @ApiModelProperty("购方名称")
    private String gfName;

    @ApiModelProperty("销方名称")
    private String xfName;

    @ApiModelProperty("购方税号")
    private String gfTaxNo;

    @ApiModelProperty("销方税号")
    private String xfTaxNo;


}
