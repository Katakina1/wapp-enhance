package com.xforceplus.wapp.modules.deduct.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductBaseResponse;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductListResponse;
import com.xforceplus.wapp.modules.deduct.excelconverter.Converter;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Describe: 供应商侧-索赔业务单导出model
 *
 * @Author xiezhongyong
 * @Date 2022/9/18
 */
@Data
public class ExportSellerClaimBillModel {

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
	
	@ExcelProperty(value = "业务单状态",converter = Converter.QueryTab.class)
	private QueryDeductBaseResponse.QueryTabResp queryTab;
	
	@ExcelProperty("关联结算单号")
	private String refSettlementNo;

}
