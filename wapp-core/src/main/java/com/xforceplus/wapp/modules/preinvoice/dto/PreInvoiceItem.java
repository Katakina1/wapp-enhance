package com.xforceplus.wapp.modules.preinvoice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PreInvoiceItem {
    /**
     * 预制发票id
     */

    private Long preInvoiceId;

    /**
     * 税收分类编码
     */

    private String goodsTaxNo;

    /**
     * 货物或应税劳务名称
     */

    private String cargoName;

    /**
     * 货物或应税劳务代码
     */

    private String cargoCode;

    /**
     * 规格型号
     */

    private String itemSpec;

    /**
     * 不含税单价
     */

    private BigDecimal unitPrice;

    /**
     * 数量
     */

    private BigDecimal quantity;

    /**
     * 单位

     */

    private String quantityUnit;

    /**
     * 税率 目前整数存储，需要程序单独处理
     1---1%
     9---9%

     */

    private BigDecimal taxRate;

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
     * 编码版本号
     */

    private String goodsNoVer;

    /**
     * 是否享受税收优惠政策 0 否 1 是
     */

    private String taxPre;

    /**
     * 是否享受税收优惠政策内容
     */

    private String taxPreCon;

    /**
     * 零税率标志 空 - 非0税率，0-出口退税，1-免税，2-不征税，3-普通0税率
     */

    private String zeroTax;

    /**
     * 是否打印单价数量 0 否 1 是
     */

    private String printContentFlag;

    /**
     * 分类码
     */

    private String itemTypeCode;

    private String priceMethod;

    private Long id;
}
