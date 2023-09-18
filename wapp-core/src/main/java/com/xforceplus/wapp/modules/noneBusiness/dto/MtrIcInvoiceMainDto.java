package com.xforceplus.wapp.modules.noneBusiness.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Setter
@Getter
public class MtrIcInvoiceMainDto implements Serializable {
    private final static long serialVersionUID = 1L;
    @ApiModelProperty("发票代码")
    private String invoiceCode;
    @ApiModelProperty("发票号码")
    private String invoiceNo;
    @ApiModelProperty("发票金额")
    private String invoiceAmount;

    @ApiModelProperty("发票开票日期")
    private String paperDate;
    @ApiModelProperty("价税合计")
    private String totalAmount;
    @ApiModelProperty("税额")
    private String taxAmount;
    @ApiModelProperty("税率")
    private String taxRate;
    @ApiModelProperty("发票类型 10 增值税电子普通发票 08 增值税电子专用发票")
    private String invoiceType;
    @ApiModelProperty("购方名称")
    private String gfName;
    @ApiModelProperty("购方税号")
    private String gfTaxNo;
    @ApiModelProperty("购方电话地址")
    private String gfAddressAndPhone;
    @ApiModelProperty("购方银行账号")
    private String gfBankAndNo;
    @ApiModelProperty("校验码")
    private String checkCode;
    @ApiModelProperty("销方税号")
    private String xfName;
    @ApiModelProperty("销方税号")
    private String xfTaxNo;
    @ApiModelProperty("销方电话地址")
    private String xfAddressAndPhone;
    @ApiModelProperty("销方银行账号")
    private String xfBankAndNo;
    @ApiModelProperty("是否带销货清单")
    private String goodsListFlag;
    @ApiModelProperty("机器码")
    private String machinecode;
    @ApiModelProperty("发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常")
    private String invoiceStatus;
    @ApiModelProperty("供应商号")
    private String venderid;

    private String jvcode;
    private String companyCode;


}