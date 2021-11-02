package com.xforceplus.wapp.modules.backFill.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by SunShiyong on 2021/10/18.
 */
@ApiModel(description = "发票对象")
@Data
public class RecordInvoiceResponse {
    /**
     * 发票类型 01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票
     */
    @ApiModelProperty("发票类型 01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票")
    private String invoiceType;

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
    private Date invoiceDate;

    /**
     * 购方税号
     */
    @ApiModelProperty("购方税号")
    private String gfTaxNo;

    /**
     * 购方名称
     */
    @ApiModelProperty("购方名称")
    private String gfName;

    /**
     * 购方地址电话
     */
    @ApiModelProperty("购方地址电话")
    private String gfAddressAndPhone;

    /**
     * 购方开户行及账号
     */
    @ApiModelProperty("购方开户行及账号")
    private String gfBankAndNo;

    /**
     * 销方税号
     */
    @ApiModelProperty("销方税号")
    private String xfTaxNo;

    /**
     * 销方名称
     */
    @ApiModelProperty("销方名称")
    private String xfName;

    /**
     * 销方地址及电话
     */
    @ApiModelProperty("销方地址及电话")
    private String xfAddressAndPhone;

    /**
     * 销方开户行及账号
     */
    @ApiModelProperty("销方开户行及账号")
    private String xfBankAndNo;

    /**
     * 金额
     */
    @ApiModelProperty("金额")
    private BigDecimal invoiceAmount;

    /**
     * 税额
     */
    @ApiModelProperty("税额")
    private BigDecimal taxAmount;

    /**
     * 价格合计
     */
    @ApiModelProperty("价格合计")
    private BigDecimal totalAmount;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常 5-蓝冲
     */
    @ApiModelProperty("发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常 5-蓝冲")
    private String invoiceStatus;

    /**
     * 发票状态修改时间
     */
    @ApiModelProperty("发票状态修改时间")
    private Date statusUpdateDate;

    /**
     * 发票状态最后修改时间
     */
    @ApiModelProperty("发票状态最后修改时间")
    private Date lastUpdateDate;

    /**
     * 签收时间
     */
    @ApiModelProperty("签收时间")
    private Date qsDate;

    /**
     * 认证方式 1-勾选认证 2-扫描认证
     */
    @ApiModelProperty("认证方式 1-勾选认证 2-扫描认证")
    private String rzhType;

    /**
     * 底账来源  0-采集 1-查验 2-录入
     */
    @ApiModelProperty("底账来源  0-采集 1-查验 2-录入")
    private String sourceSystem;

    /**
     * 是否有效 1-有效 0-无效
     */
    @ApiModelProperty("是否有效 1-有效 0-无效")
    private String valid;

    /**
     * 发票代码+发票号码    唯一索引 防重复
     */
    @ApiModelProperty("发票代码+发票号码    唯一索引 防重复")
    private String uuid;

    /**
     * 签收方式（0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收5-pdf上传签收
     */
    @ApiModelProperty("签收方式（0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收5-pdf上传签收")
    private String qsType;

    /**
     * 签收结果（0-未签收 1-已签收）
     */
    @ApiModelProperty("签收结果（0-未签收 1-已签收）")
    private String qsStatus;

    /**
     * 校验码
     */
    @ApiModelProperty("校验码")
    private String checkCode;

    /**
     * 通行费标志(Y-可抵扣通行费，N-不可抵扣通行费)
     */
    @ApiModelProperty("Y-可抵扣通行费，N-不可抵扣通行费")
    private String txfbz;

    /**
     * 零税率标志 (空:非零税率，1:税率栏位显示“免税”，2:税率栏位显示“不征收”，3:零税率
     */
    @ApiModelProperty("零税率标志 (空:非零税率，1:税率栏位显示“免税”，2:税率栏位显示“不征收”，3:零税率")
    private String lslbz;


    /**
     * 结算金额
     */
    @ApiModelProperty("结算金额")
    private BigDecimal settlementAmount;


    /**
     * 可红冲金额
     */
    @ApiModelProperty("可红冲金额")
    private BigDecimal redMoneyAmount;

    /**
     * 付款金额
     */
    @ApiModelProperty("付款金额")
    private BigDecimal paymentAmount;

    /**
     * 公司代码
     */
    @ApiModelProperty("公司代码")
    private String companyCode;


    /**
     * 是否删除 0-未删除 1--已删除
     */
    @ApiModelProperty("是否删除 0-未删除 1--已删除")
    private String isDel;

    /**
     * 删除时间
     */
    @ApiModelProperty("删除时间")
    private Date delDate;

    /**
     * BPMS付款状态 0--未付款 1-已付款
     */
    @ApiModelProperty("BPMS付款状态 0--未付款 1-已付款")
    private String bpmsPayStatus;
    /**
     * 结算单号
     */
    @ApiModelProperty("结算单号")
    private String settlementNo;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("红字信息编号")
    private String redNotificationNo;

    @ApiModelProperty("税率")
    private String taxRate;



}
