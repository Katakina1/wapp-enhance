package com.xforceplus.wapp.modules.rednotification.model;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Data
@ApiModel("红字信息表主信息")
public class RedNotificationMain {
    @ApiModelProperty("预制发票id")
    private String pid;

    @ApiModelProperty("备注")
    private String remark;


    @ApiModelProperty("1 销方 2购方")
    private Integer userRole;

    @ApiModelProperty("申请类型 购方发起:0-已抵扣1-未抵扣 销方发起:2-开票有误")
    private Integer applyType;

    @ApiModelProperty("申请原因")
    private String applyReason;


    @ApiModelProperty("发票类型 c增值税普通发票 s 增值税专用发票 se 增值税电子专用发票 ce 增值税电子普通发票")
    private String invoiceType;

    @ApiModelProperty("原发票类型 c增值税普通发票 s 增值税专用发票 se 增值税电子专用发票 ce 增值税电子普通发票")
    private String originInvoiceType;

    @ApiModelProperty("原发票代码")
    private String originalInvoiceCode;

    @ApiModelProperty("原发票号码")
    private String originalInvoiceNo;

    @ApiModelProperty("原开票日期")
    private String originalInvoiceDate;


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
     * 销方税号
     */
    @ApiModelProperty("销方税号")
    private String sellerTaxNo;

    /**
     * 销方名称
     */
    @ApiModelProperty("销方名称")
    private String sellerName;

    @ApiModelProperty("不含税金额")
    private BigDecimal amountWithoutTax;

    @ApiModelProperty("含税金额")
    private BigDecimal amountWithTax;

    @ApiModelProperty("税额")
    private BigDecimal taxAmount;


    /**
     * 扣除额
     */
    @ApiModelProperty("扣除额")
    private BigDecimal deduction;


    @ApiModelProperty("单号")
    private String billNo;

    /**
     * 红字信息来源1.索赔单，2协议单，3.EPD
     */
    @ApiModelProperty("红字信息来源1.索赔单，2协议单，3.EPD ,4 导入")
    private Integer invoiceOrigin;

    /**
     * 红字信息表编号
     */
    @ApiModelProperty("红字信息表编号")
    private String redNotificationNo;



    /**
     * 公司编号
     */
    @ApiModelProperty("公司编号")
    private String companyCode;


    /**
     * 特殊发票标记 0-默认  1-通行费   2-成品油
     */
    @ApiModelProperty("special_invoice_flag")
    private Integer specialInvoiceFlag;
    
    /**
     * 成品油操作类型(成品油时必填)
	0-成品油涉及销售数量变更 （单位、数量和单价必填）
	1-成品油仅涉及销售金额变更 （单位、数量和单价为空）
     */
    @ApiModelProperty("oil_memo")
    private Integer oilMemo;

    /**
     * 客户编号
     */
    @ApiModelProperty("集成平台编号")
    private String customerNo;

    @ApiModelProperty("扣款时间")
    private Date paymentTime;

    //申请人  申请电话
    @ApiModelProperty("申请人")
    private String applyPerson;

    @ApiModelProperty("申请人电话")
    private String applyPersonTel;

    @ApiModelProperty("申请日期")
    private String invoiceDate;

    @ApiModelProperty("创建时间")
    private Date createDate;

    @ApiModelProperty("申请失败原因")
    private String applyRemark;


    @ApiModelProperty("审批状态 1. 审核通过,2. 审核不通过,3. 已核销,4. 已撤销,5.撤销待审批")
    private Integer approveStatus;




    /**
     * 申请流水号
     */
    @ApiModelProperty("申请流水号")
    private String serialNo;

    //============== 新增不需要====
    @ApiModelProperty("红字信息主键")
    private Long id;

    /**
     * 红字信息表编号
     */
    @ApiModelProperty("计价方式")
    private String priceMethod;

    /**
     * 驳回备注
     */
    @ApiModelProperty("驳回备注")
    private String rejectRemark;

    /**
     * 撤销备注
     */
    @ApiModelProperty("撤销备注")
    private String revertRemark;

    /**
     * 撤销时间
     */
    @ApiModelProperty("撤销时间")
    private Date cancelTime;

}
