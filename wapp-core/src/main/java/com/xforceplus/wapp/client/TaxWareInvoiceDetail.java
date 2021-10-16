package com.xforceplus.wapp.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

/**
 * @author masp mashaopeng@xforceplus.com
 */
@Data
public class TaxWareInvoiceDetail {
//    @JsonProperty(value = "goodsTaxNo")
//    @JsonPropertyDescription(value = "税收分类编码")
//    private String goodsTaxNo;

    @JsonProperty(value = "cargoName")
    @JsonPropertyDescription(value = "货物名称")
    private String goodsName;

    @JsonProperty(value = "itemSpec")
    @JsonPropertyDescription(value = "规格型号")
    private String goodsModel;

    @JsonProperty(value = "quantityUnit")
    @JsonPropertyDescription(value = "商品单位")
    private String goodsUnit;

    @JsonProperty(value = "quantity")
    @JsonPropertyDescription(value = "商品数量")
    private String goodsNumber;

    @JsonProperty(value = "unitPrice")
    @JsonPropertyDescription(value = "不含税单价")
    private String goodsPrice;

    @JsonProperty(value = "taxRate")
    @JsonPropertyDescription(value = "税率")
    private String taxRate;

    @JsonProperty(value = "amountWithoutTax")
    @JsonPropertyDescription(value = "不含税金额")
    private String goodsAmount;

    @JsonProperty(value = "taxAmount")
    @JsonPropertyDescription(value = "税额")
    private String taxAmount;

//    @JsonProperty(value = "amountWithTax")
//    @JsonPropertyDescription(value = "含税金额")
//    private String amountWithTax;
}
