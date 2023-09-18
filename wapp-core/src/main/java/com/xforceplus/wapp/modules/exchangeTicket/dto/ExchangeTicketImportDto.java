package com.xforceplus.wapp.modules.exchangeTicket.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author aiwentao@xforceplus.com
 */
@Data
@ApiModel("换票导入配置")
public class ExchangeTicketImportDto {

    @ExcelProperty(value = "序号", index = 0)
    private String rowNum;
    @ExcelProperty(value = "JV", index = 1)
    private String jvCode;


    @ExcelProperty(value = "Vendor", index = 2)
    private String venderId;


    @ExcelProperty(value = "Vendor Name", index = 3)
    private String venderName;


    @ExcelProperty(value = "旧发票号", index = 4)
    private String invoiceNo;
    @ExcelProperty(value = "旧票发票代码", index = 5)
    private String invoiceCode;
    @ExcelProperty(value = "旧票开票日期", index = 6)
    private String paperDate;
    @ExcelProperty(value = "旧票不含税金额", index = 7)
    private String amountWithoutTax;
    @ExcelProperty(value = "旧票税率", index = 8)
    private String taxRate;
    @ExcelProperty(value = "旧票税额", index = 9)
    private String taxAmount;
    @ExcelProperty(value = "退票原因", index = 10)
    private String exchangeReason;
    @ExcelProperty(value = "业务类型", index = 11)
    private String flowType;
    @ExcelProperty(value = "换票状态", index = 12)
    private String exchangeStatus;

    private String errorMsg;

    private String amountWithTax;

    private String xfTaxNo;

    private String invoiceId;

    private String exchangeType;

    private String exchangeSoource;
}
