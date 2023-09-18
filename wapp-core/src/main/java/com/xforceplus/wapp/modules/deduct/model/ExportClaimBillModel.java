package com.xforceplus.wapp.modules.deduct.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductBaseResponse;
import com.xforceplus.wapp.modules.deduct.excelconverter.Converter;
import com.xforceplus.wapp.modules.deduct.service.InvoiceTypeConverter;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by SunShiyong on 2021/10/22.
 */
@Data
public class ExportClaimBillModel {



    @ExcelProperty(value = "索赔单状态",converter = Converter.QueryTab.class)
    private QueryDeductBaseResponse.QueryTabResp queryTab;

    @ExcelProperty("索赔单号")
    private String businessNo;

    @ExcelProperty("结算单号")
    private String refSettlementNo;

    @ExcelProperty("扣款日期")
    private Date deductDate;

    @ExcelProperty("扣款公司")
    private String purchaserName;

    @ExcelProperty("供应商编号")
    private String sellerNo;

    @ExcelProperty("供应商名称")
    private String sellerName;

    @ExcelProperty("含税金额")
    private BigDecimal amountWithTax;

    @ExcelProperty("税率")
    private BigDecimal taxRate;

    @ExcelProperty("不含税金额")
    private BigDecimal amountWithoutTax;

    @ExcelProperty("税额")
    private BigDecimal taxAmount;

    @ExcelProperty(value = "发票类型", converter = InvoiceTypeConverter.class)
    private String invoiceType;

    @ExcelProperty("定案日期")
    private Date verdictDate;

    @ExcelProperty("批次号")
    private String batchNo;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("所扣发票")
    private String deductInvoice;

    @ExcelProperty("明细总不含税金额")
    private BigDecimal itemWithoutAmount;

    @ExcelProperty("明细总含税金额")
    private BigDecimal itemWithAmount;

    @ExcelProperty("明细总税额")
    private BigDecimal itemTaxAmount;

    @ExcelProperty(value = "红字信息表状态", converter = Converter.RedNotificationStatus.class)
    private List<Integer> redNotificationStatus;

    @ExcelProperty(value = "红字信息表编号",converter = Converter.RedNotificationNos.class)
    private List<String> redNotificationNos;

    @ExcelProperty("列外说明")
    private String exceptionDescription;




}
