package com.xforceplus.wapp.modules.invoice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class InvoiceItemDto {
    /**
     * 发票代码
     */
    private String invoiceCode;

    /**
     * 发票号码
     */
    private String invoiceNo;

    /**
     * 发票ID
     */
    private String invoiceId;

    /**
     * 货物或应税劳务代码
     */
    private String cargoCode;

    /**
     * 货物或应税劳务名称
     */
    private String cargoName;

    /**
     * 规格型号
     */
    private String itemSpec;

    /**
     * 商品单位
     */
    private String quantityUnit;

    /**
     * 商品数量
     */
    private BigDecimal quantity;

    /**
     * 发票号码
     */
    private String taxRate;

    /**
     * 不含税单价
     */
    private BigDecimal unitPrice;

    /**
     * 不含税金额
     */
    private BigDecimal amountWithoutTax;

    /**
     * 税额
     */
    private BigDecimal taxAmount;

    /**
     * 含税金额
     */
    private BigDecimal amountWithTax;

    /**
     * 税收分类编码
     */
    private String goodsTaxNo;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 车辆类型
     */
    private String vehicleType;

    /**
     * 通行日期起
     */
    private String tollStartDate;

    /**
     * 通行日期止
     */
    private String tollEndDate;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 更新日期
     */
    private Date updateTime;

    private Long id;
}
