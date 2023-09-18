package com.xforceplus.wapp.modules.deduct.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

@Data
public class VendorExportClaimBillModel {

	@ExcelProperty("序号")
	private int num;
	
	@ExcelProperty("索赔单号")
	private String businessNo;
	
	@ExcelProperty("红字信息表编号")
	private String redNotificationNo;
	
	@ExcelProperty("定案日期")
	private Date verdictDate;
	
	@ExcelProperty("扣款日期")
	private Date deductDate;
	
	@ExcelProperty("扣款")
	private String purchaserNo;
	
	@ExcelProperty("供应商编号")
	private String sellerNo;
	
	@ExcelProperty("税率")
	private BigDecimal taxRate;
	
	@ExcelProperty("不含税金额")
	private BigDecimal amountWithoutTax;
	
	@ExcelProperty("含税金额")
	private BigDecimal amountWithTax;
	
	@ExcelProperty("批次号")
	private String batchNo;
	
	@ExcelProperty("扣款发票")
	private String deductInvoice;
	
	@ExcelProperty("业务单状态")
	private String statusStr;
	
	@ExcelProperty("关联结算单号")
	private String refSettlementNo;

}
