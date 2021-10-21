package com.xforceplus.wapp.modules.backFill.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-09-15 20:23
 **/
@Setter
@Getter
@ApiModel("发票详情实体")
public class InvoiceDetail {
    /**
     * 货名
     */
    private String cargoName;

    /**
     * 型号规格
     */
    private String itemSpec;

    /**
     * 数量单位
     */
    private String quantityUnit;

    /**
     * 数量
     */
    private String quantity;

    /**
     * 单价
     */
    private String unitPrice;

    /**
     * 税率
     */
    private String taxRate;

    /**
     * 零税率标志 空-非0税率；0-出口退税1-免税2-不征税3-普通0税率 4-差额征税
     */
    private String zeroTax;

    /**
     * 不含税金额
     */
    private String amountWithoutTax;

    /**
     * 税额
     */
    private String taxAmount;

    /**
     * 含税金额
     */
    private String amountWithTax;

}
