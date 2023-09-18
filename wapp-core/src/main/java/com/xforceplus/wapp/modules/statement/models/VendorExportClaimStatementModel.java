package com.xforceplus.wapp.modules.statement.models;

import java.io.Serializable;
import java.math.BigDecimal;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class VendorExportClaimStatementModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@ExcelProperty("序号")
	private int num;
	
	@ExcelProperty("结算单号")
    private String settlementNo;
	
	@ExcelProperty("索赔号")
    private String businessNo;
	
	@ExcelProperty("含税金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amountWithTax;

	@ExcelProperty("不含税金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amountWithoutTax;

	@ExcelProperty("税额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal taxAmount;
    
	@ExcelProperty("供应商编码")
    private String sellerNo;
    
	@ExcelProperty("购方编码")
    private String purchaserNo;
    
	@ExcelProperty("发票类型")
    private String invoiceType;
    
	@ExcelProperty("结算单状态")
    private String settlementStatus;
    
}
