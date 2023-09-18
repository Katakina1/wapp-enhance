package com.xforceplus.wapp.modules.exchangeTicket.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xforceplus.wapp.modules.exchange.model.BusinessStatusConver;
import com.xforceplus.wapp.modules.exchange.model.ExchangeStatusConver;
import com.xforceplus.wapp.modules.exchangeTicket.convert.ExchangeAmountConver;
import com.xforceplus.wapp.modules.exchangeTicket.convert.ExchangeSourceConver;
import com.xforceplus.wapp.modules.exchangeTicket.convert.ExchangeTypeConver;
import com.xforceplus.wapp.modules.noneBusiness.convert.AuthStatusConver;
import com.xforceplus.wapp.modules.noneBusiness.convert.TaxRateConverter;
import lombok.Data;


@Data
public class ExchangeTicketExportDto {

    @ExcelProperty(value = "发票号码")
    private String invoiceNo;
    @ExcelProperty(value = "发票代码")
    private String invoiceCode;
    @ExcelProperty(value = "开票日期")
    private String paperDate;
    @ExcelProperty(value = "换票开票日期")
    private String exchangePaperDate;
    @ExcelProperty(value = "凭证号")
    private String voucherNo;
    @ExcelProperty(value = "JVCODE")
    private String jvCode;
    @ExcelProperty(value = "供应商号")
    private String venderId;
    @ExcelProperty(value = "供应商名称")
    private String venderName;

    @ExcelProperty(value = "不含税金额",converter = ExchangeAmountConver.class)
    private String amountWithoutTax;
    @ExcelProperty(value = "税率", converter = TaxRateConverter.class)
    private String taxRate;
    @ExcelProperty(value = "税额",converter = ExchangeAmountConver.class)
    private String taxAmount;
    @ExcelProperty(value = "价税合计",converter = ExchangeAmountConver.class)
    private String amountWithTax;
    @ExcelProperty(value = "换票后发票号码")
    private String exchangeInvoiceNo;
    @ExcelProperty(value = "换票后发票代码")
    private String exchangeInvoiceCode;


    @ExcelProperty(value = "换票后税额",converter = ExchangeAmountConver.class)
    private String exchangeTaxAmount;
    @ExcelProperty(value = "换票后税率",converter = TaxRateConverter.class)
    private String exchangeTaxRate;
    @ExcelProperty(value = "换票后不含税金额",converter = ExchangeAmountConver.class)
    private String exchangeAmountWithoutTax;
    @ExcelProperty(value = "换票后价税合计",converter = ExchangeAmountConver.class)
    private String exchangeAmountWithTax;
    @ExcelProperty(value = "认证状态",converter =AuthStatusConver.class)
    private String authStatus;
    @ExcelProperty(value = "退票认证状态",converter =AuthStatusConver.class)
    private String exchangeAuthStatus;
    @ExcelProperty(value = "换票状态", converter = ExchangeStatusConver.class)
    private String exchangeStatus;

    @ExcelProperty(value = "换票来源",converter = ExchangeSourceConver.class)
    private String exchangeSoource;
    @ExcelProperty(value = "换票类型",converter = ExchangeTypeConver.class)
    private String exchangeType;
    @ExcelProperty(value = "换票原因")
    private String exchangeReason;
    @ExcelProperty(value = "换票审核结果")
    private String exchangeRemark;
    @ExcelProperty(value = "换票商品类型",converter = BusinessStatusConver.class)
    private String flowType;
    @ExcelProperty(value = "创建人")
    private String createUser;
    @ExcelProperty(value = "创建时间")
    private String createDateStr;
    @ExcelProperty(value = "确认时间")
    private String lastUpdateDateStr;


}
