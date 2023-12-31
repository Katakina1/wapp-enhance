package com.xforceplus.wapp.modules.backfill.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by SunShiyong on 2021/10/21.
 */
@ApiModel(description = "正式发票详情响应对象")
@Data
    public class InvoiceDetailResponse {

    @ApiModelProperty("发票详情列表")
    private List<InvoiceDetail> items;
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
    private Date paperDrewDate;

    /**
     * 购方税号
     */
    @ApiModelProperty("购方税号")
    private String purchaserTaxNo;

    /**
     * 购方名称
     */
    @ApiModelProperty("购方名称")
    private String purchaserName;

    /**
     * 购方地址电话
     */
    @ApiModelProperty("购方地址电话")
    private String purchaserAddressAndPhone;

    /**
     * 购方开户行及账号
     */
    @ApiModelProperty("购方开户行及账号")
    private String purchaserBankAndNo;

    /**
     * 销方税号
     */
    @ApiModelProperty("销方税号")
    private String sellerTaxNo;

    /**
     * 销方名称
     */
    @ApiModelProperty("销方名称")
    private String sellerName;

    /**
     * 销方地址及电话
     */
    @ApiModelProperty("销方地址及电话")
    private String sellerAddressAndPhone;

    /**
     * 销方开户行及账号
     */
    @ApiModelProperty("销方开户行及账号")
    private String sellerBankAndNo;

    /**
     * 金额
     */
    @ApiModelProperty("不含税金额")
    private BigDecimal amountWithoutTax;

    /**
     * 税额
     */
    @ApiModelProperty("税额")
    private BigDecimal taxAmount;

    /**
     * 价格合计
     */
    @ApiModelProperty("含税金额")
    private BigDecimal amountWithTax;

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
     * 结算金额
     */
    @ApiModelProperty("结算金额")
    private BigDecimal settlementAmount;


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

    @ApiModelProperty("密文")
    private String cipherText;

    @ApiModelProperty("机器码")
    private String machineCode;

    @ApiModelProperty("销货清单标志  1-有销货清单")
    private String goodsListFlag;

    @ApiModelProperty("orgcode")
    private String jvcode;

    @ApiModelProperty("供应商编码")
    private String sellerNo;

    @ApiModelProperty("是否认证 0-未认证 1-已认证")
    private String rzhYesorno;

    @ApiModelProperty("换票原因")
    private String exchangeReason;

    @ApiModelProperty("换票状态 0初始 1待换票 2已上传 3已完成")
    private Integer exchangeStatus;

}
