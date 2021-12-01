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
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 底账发票id
     */
    @ApiModelProperty("底账发票id")
    private Long invoiceId;

    /**
     * 状态 0待换票 1已上传 2已完成 9删除
     */
    @ApiModelProperty("状态 0待换票 1已上传 2已完成 9删除")
    private Boolean status;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

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
     * 税额
     */
    @ApiModelProperty("税额")
    private BigDecimal taxAmount;

    /**
     * 业务类型
     */
    @ApiModelProperty("业务类型")
    private String businessType;

    /**
     * 退单号
     */
    @ApiModelProperty("退单号")
    private String returnNo;

    /**
     * 快递公司
     */
    @ApiModelProperty("快递公司")
    private String expressCompany;

    /**
     * 快递单号
     */
    @ApiModelProperty("快递单号")
    private String waybillNo;

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

    /**
     * 新开发票id，逗号隔开
     */
    @ApiModelProperty("新开发票id，逗号隔开")
    private String newInvoiceId;

    @ApiModelProperty("换票原因")
    private String remark;

    @ApiModelProperty("发票类型")
    private String invoiceType;

    @ApiModelProperty("纸电类型：true电票 false纸票")
    private boolean isElectronic;


}
