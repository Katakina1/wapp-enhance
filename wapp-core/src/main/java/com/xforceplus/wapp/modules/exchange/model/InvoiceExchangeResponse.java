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

    /**
     * 主键
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 底账发票id
     */
    @ApiModelProperty("invoice_id")
    private Long invoiceId;

    /**
     * 状态 0待换票 1已上传 2已完成 9删除
     */
    @ApiModelProperty("status")
    private Boolean status;

    /**
     * 创建时间
     */
    @ApiModelProperty("create_time")
    private Date createTime;

    /**
     * orgcode
     */
    @ApiModelProperty("jvcode")
    private String jvcode;

    /**
     * 供应商编码
     */
    @ApiModelProperty("seller_no")
    private String sellerNo;

    /**
     * 不含税金额
     */
    @ApiModelProperty("amount_without_tax")
    private BigDecimal amountWithoutTax;

    /**
     * 税额
     */
    @ApiModelProperty("tax_amount")
    private BigDecimal taxAmount;

    /**
     * 业务类型
     */
    @ApiModelProperty("business_type")
    private String businessType;

    /**
     * 退单号
     */
    @ApiModelProperty("return_no")
    private String returnNo;

    /**
     * 快递公司
     */
    @ApiModelProperty("express_company")
    private String expressCompany;

    /**
     * 快递单号
     */
    @ApiModelProperty("waybill_no")
    private String waybillNo;

    /**
     * 发票代码
     */
    @ApiModelProperty("invoice_code")
    private String invoiceCode;

    /**
     * 发票号码
     */
    @ApiModelProperty("invoice_no")
    private String invoiceNo;

    /**
     * 开票日期
     */
    @ApiModelProperty("paper_drew_date")
    private String paperDrewDate;

    /**
     * 新开发票id，逗号隔开
     */
    @ApiModelProperty("new_invoice_id")
    private String newInvoiceId;


}
