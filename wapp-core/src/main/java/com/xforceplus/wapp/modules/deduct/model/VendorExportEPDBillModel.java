package com.xforceplus.wapp.modules.deduct.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

@Data
public class VendorExportEPDBillModel {
	
	@ExcelProperty("序号")
	private int num;
	
	@ExcelProperty("协议号")
	private String businessNo;
	
	@ExcelProperty("红字信息表编号")
	private String redNotificationNo;

	@ExcelProperty("协议状态")
	private String statusStr;
	
	@ExcelProperty("扣款日期")
	private Date deductDate;
	
	@ExcelProperty("客户名称")
	private String sellerName;
	
	@ExcelProperty("协议类型")
	private String agreementDocumentType;
	
	@ExcelProperty("税率")
	private BigDecimal taxRate;
	
	@ExcelProperty("含税金额")
	private BigDecimal amountWithTax;
	
	@ExcelProperty("不含税金额")
	private BigDecimal amountWithoutTax;
	
	@ExcelProperty("税码")
	private String agreementTaxCode;
	
	@ExcelProperty(value = "锁定状态")
	private String lockStr;
	
	@ExcelProperty(value = "是否超期")
	private String overdueStr;
	
	@ExcelProperty("关联结算单号")
	private String refSettlementNo;
	
}
