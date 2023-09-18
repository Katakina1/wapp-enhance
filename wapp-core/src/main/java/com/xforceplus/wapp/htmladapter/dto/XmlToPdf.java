package com.xforceplus.wapp.htmladapter.dto;

import lombok.Data;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
public class XmlToPdf {
    List<PdfInvoice> invoiceMain;

    @Data
    public static class PdfInvoice {
        // 发票类型
        private String invoiceType;
        // 发票号码
        private String invoiceNo;
        // 开票日期
        private String paperDrewDate;
        // 购方税号
        private String purchaserTaxNo;
        // 购方名称
        private String purchaserName;
        // 销方税号
        private String sellerTaxNo;
        // 销方名称
        private String sellerName;
        // 金额
        private String amountWithoutTax;
        // 税额
        private String taxAmount;
        // 价格合计
        private String amountWithTax;
        // 价格合计大写
        private String amountWithTaxCn;
        // 备注
        private String remark;
        // 开票人
        private String drawerName;

        private List<PdfInvoiceDetail> invDetails;
    }

    @Data
    public static class PdfInvoiceDetail {
        //货物或应税劳务名称
        private String cargoName;
        //规格型号
        private String itemSpec;
        //单位
        private String quantityUnit;
        //数量
        private String quantity;
        //单价
        private String unitPrice;
        //金额
        private String amountWithoutTax;
        //税率
        private String taxRate;
        //税额
        private String taxAmount;
    }

}
