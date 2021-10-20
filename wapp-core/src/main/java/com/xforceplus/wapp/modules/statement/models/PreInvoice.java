package com.xforceplus.wapp.modules.statement.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@ApiModel("待开票信息")
public class PreInvoice {
    @ApiModelProperty("唯一id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @ApiModelProperty("结算单编码")
    private String settlementNo;
    @ApiModelProperty("购方编码")
    private String purchaserNo;
    @ApiModelProperty("购方名称")
    private String purchaserName;
    @ApiModelProperty("购方税号")
    private String purchaserTaxNo;
    @ApiModelProperty("购方电话")
    private String purchaserTel;
    @ApiModelProperty("购方地址")
    private String purchaserAddress;
    @ApiModelProperty("购方开户行")
    private String purchaserBankName;
    @ApiModelProperty("购方银行账号")
    private String purchaserBankAccount;
    @ApiModelProperty("供应商编码")
    private String sellerNo;
    @ApiModelProperty("供应商税编")
    private String sellerTaxNo;
    @ApiModelProperty("销方名称")
    private String sellerName;
    @ApiModelProperty("销方电话")
    private String sellerTel;
    @ApiModelProperty("供应商地址")
    private String sellerAddress;
    @ApiModelProperty("供应商开户行")
    private String sellerBankName;
    @ApiModelProperty("供应商银行账号")
    private String sellerBankAccount;
    @ApiModelProperty("发票类型")
    private String invoiceType;
    @ApiModelProperty("结算单类型:1.索赔单、2.协议单、3.EPD单")
    private Integer settlementType;
    @ApiModelProperty("发票号码")
    private String invoiceNo;
    @ApiModelProperty("发票代码")
    private String invoiceCode;
    @ApiModelProperty("开票日期")
    private String paperDrawDate;
    @ApiModelProperty("机器码")
    private String machineCode;
    @ApiModelProperty("校验码")
    private String checkCode;
    @ApiModelProperty("不含税金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amountWithoutTax;
    @ApiModelProperty("税额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal taxAmount;
    @ApiModelProperty("含税金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amountWithTax;
    @ApiModelProperty("税率")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal taxRate;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("预制发票状态:1.待申请、2.待上传、3.已上传、4.待审核、5.已撤销、")
    private Integer preInvoiceStatus;
    @ApiModelProperty("开具结果")
    private String processRemark;
    @ApiModelProperty("开票规则ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long ruleId;
    @ApiModelProperty("红票的原始号码")
    private String originInvoiceNo;
    @ApiModelProperty("红票的原始号码")
    private String originInvoiceCode;
    @ApiModelProperty("原始发票类型:01.专纸、03.机动车、04.普纸、08.专电、10.普电、11.卷票、14.通行费电票")
    private String originInvoiceType;
    @ApiModelProperty("原始发票开票日期")
    private String originPaperDrawDate;
    @ApiModelProperty("红字信息编码")
    private String redNotificationNo;
    @ApiModelProperty("红字信息编号申请状态:0.未申请、1.发起申请、2.已申请")
    private Integer redNotificationFlag;
    @ApiModelProperty("更新时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long updateTime;
}
