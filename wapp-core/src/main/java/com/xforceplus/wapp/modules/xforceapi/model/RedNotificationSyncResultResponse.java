package com.xforceplus.wapp.modules.xforceapi.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class RedNotificationSyncResultResponse extends XforceApiResponse implements Serializable {

	private static final long serialVersionUID = -3382904128661454543L;

	private Result result;

	public static class Result {
		// 流水号
		private String serialNo;
		// 处理结果标识(0:失败1:成功)
		private String processFlag;
		// 处理结果描述
		private String processRemark;
		// 红字信息表列表
		private List<RedNotification> redNotificationList;

		public String getSerialNo() {
			return serialNo;
		}

		public void setSerialNo(String serialNo) {
			this.serialNo = serialNo;
		}

		public String getProcessFlag() {
			return processFlag;
		}

		public void setProcessFlag(String processFlag) {
			this.processFlag = processFlag;
		}

		public String getProcessRemark() {
			return processRemark;
		}

		public void setProcessRemark(String processRemark) {
			this.processRemark = processRemark;
		}

		public List<RedNotification> getRedNotificationList() {
			return redNotificationList;
		}

		public void setRedNotificationList(List<RedNotification> redNotificationList) {
			this.redNotificationList = redNotificationList;
		}

	}

	public static class RedNotification {
		// 非必须 原发票代码
		private String originalInvoiceCode;// ": "",
		// 非必须 原发票号码
		private String originalInvoiceNo;// ": "",
		// 非必须 购方税号
		private String purchaseTaxCode;// ": "914403007109368585",
		// 非必须 购方税号
		private String purchaserTaxCode;// ": "914403007109368585",
		// 非必须 购方名称
		private String purchaserName;// ": "沃尔玛（中国）投资有限公司",
		// 非必须 红字信息编号
		private String redNotificationNo;// ": "4403042302287829",
		// 非必须 销方税号
		private String sellerTaxCode;// ": "91110113MA004DX985",
		// 非必须 销方名称
		private String sellerName;// ": "北京市恒通融商国际贸易有限公司",
		// 非必须 状态
		private String statusCode;// ": "TZD0082",
		// 非必须 状态描述
		private String statusMsg;// ": "已撤销",
		// 金额信息
		private Amount amount;
		// 非必须填开日期，格式：yyyyMMdd
		private String applyDate;// ": "20230222",
		//
		private String companyTaxNo;// ": null,
		// 红字信息表明细
		private List<Detail> detailList;

		public String getOriginalInvoiceCode() {
			return originalInvoiceCode;
		}

		public void setOriginalInvoiceCode(String originalInvoiceCode) {
			this.originalInvoiceCode = originalInvoiceCode;
		}

		public String getOriginalInvoiceNo() {
			return originalInvoiceNo;
		}

		public void setOriginalInvoiceNo(String originalInvoiceNo) {
			this.originalInvoiceNo = originalInvoiceNo;
		}

		public String getPurchaseTaxCode() {
			return purchaseTaxCode;
		}

		public void setPurchaseTaxCode(String purchaseTaxCode) {
			this.purchaseTaxCode = purchaseTaxCode;
		}

		public String getPurchaserTaxCode() {
			return purchaserTaxCode;
		}

		public void setPurchaserTaxCode(String purchaserTaxCode) {
			this.purchaserTaxCode = purchaserTaxCode;
		}

		public String getPurchaserName() {
			return purchaserName;
		}

		public void setPurchaserName(String purchaserName) {
			this.purchaserName = purchaserName;
		}

		public String getRedNotificationNo() {
			return redNotificationNo;
		}

		public void setRedNotificationNo(String redNotificationNo) {
			this.redNotificationNo = redNotificationNo;
		}

		public String getSellerTaxCode() {
			return sellerTaxCode;
		}

		public void setSellerTaxCode(String sellerTaxCode) {
			this.sellerTaxCode = sellerTaxCode;
		}

		public String getSellerName() {
			return sellerName;
		}

		public void setSellerName(String sellerName) {
			this.sellerName = sellerName;
		}

		public String getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(String statusCode) {
			this.statusCode = statusCode;
		}

		public String getStatusMsg() {
			return statusMsg;
		}

		public void setStatusMsg(String statusMsg) {
			this.statusMsg = statusMsg;
		}

		public Amount getAmount() {
			return amount;
		}

		public void setAmount(Amount amount) {
			this.amount = amount;
		}

		public String getApplyDate() {
			return applyDate;
		}

		public void setApplyDate(String applyDate) {
			this.applyDate = applyDate;
		}

		public String getCompanyTaxNo() {
			return companyTaxNo;
		}

		public void setCompanyTaxNo(String companyTaxNo) {
			this.companyTaxNo = companyTaxNo;
		}

		public List<Detail> getDetailList() {
			return detailList;
		}

		public void setDetailList(List<Detail> detailList) {
			this.detailList = detailList;
		}

	}

	// 金额信息
	public static class Amount {
		// 价税合计（保留小数点后2位）
		private BigDecimal amountWithTax;// ": -112991.45,
		// 不含税金额（保留小数点后2位）
		private BigDecimal amountWithoutTax;// ": -99992.44,
		// 税额（保留小数点后2位）
		private BigDecimal taxAmount;// ": -12999.01

		public BigDecimal getAmountWithTax() {
			return amountWithTax;
		}

		public void setAmountWithTax(BigDecimal amountWithTax) {
			this.amountWithTax = amountWithTax;
		}

		public BigDecimal getAmountWithoutTax() {
			return amountWithoutTax;
		}

		public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
			this.amountWithoutTax = amountWithoutTax;
		}

		public BigDecimal getTaxAmount() {
			return taxAmount;
		}

		public void setTaxAmount(BigDecimal taxAmount) {
			this.taxAmount = taxAmount;
		}

	}

	public static class Detail {
		// 明细金额信息
		private DetailAmount detailAmount;
		// 发票明细商品或劳务信息
		private DetailProduction production;
		// 税收信息
		private DetailTax tax;
		// 含税标识：单价是否含税
		private boolean useWithTax;

		public DetailAmount getDetailAmount() {
			return detailAmount;
		}

		public void setDetailAmount(DetailAmount detailAmount) {
			this.detailAmount = detailAmount;
		}

		public DetailProduction getProduction() {
			return production;
		}

		public void setProduction(DetailProduction production) {
			this.production = production;
		}

		public DetailTax getTax() {
			return tax;
		}

		public void setTax(DetailTax tax) {
			this.tax = tax;
		}

		public boolean isUseWithTax() {
			return useWithTax;
		}

		public void setUseWithTax(boolean useWithTax) {
			this.useWithTax = useWithTax;
		}

	}

	public static class DetailAmount {
		// 数量（保留小数点后10位）
		private BigDecimal quantity;// ": null,
		// 单价（最多保留小数点后15位）
		private BigDecimal unitPrice;// ": null,
		// 不含税金额（保留小数点后2位）
		private BigDecimal amountWithoutTax;// ": -99992.44,
		// 含税金额（保留小数点后2位）
		private BigDecimal amountWithTax;// ": null,
		// 税额（保留小数点后2位）
		private BigDecimal taxAmount;// ": -12999.01

		public BigDecimal getQuantity() {
			return quantity;
		}

		public void setQuantity(BigDecimal quantity) {
			this.quantity = quantity;
		}

		public BigDecimal getUnitPrice() {
			return unitPrice;
		}

		public void setUnitPrice(BigDecimal unitPrice) {
			this.unitPrice = unitPrice;
		}

		public BigDecimal getAmountWithoutTax() {
			return amountWithoutTax;
		}

		public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
			this.amountWithoutTax = amountWithoutTax;
		}

		public BigDecimal getAmountWithTax() {
			return amountWithTax;
		}

		public void setAmountWithTax(BigDecimal amountWithTax) {
			this.amountWithTax = amountWithTax;
		}

		public BigDecimal getTaxAmount() {
			return taxAmount;
		}

		public void setTaxAmount(BigDecimal taxAmount) {
			this.taxAmount = taxAmount;
		}
	}

	public static class DetailProduction {
		// 货物或应税劳务编码(税编)
		private String productionCode;// ": "",
		// 货物或应税劳务名称
		private String productionName;// ": "详见对应正数发票及清单",
		// 规格型号
		private String specification;// ": "",
		// 规格型号
		private String unitName;// ": "",
		//
		private String commodityCode;// ": null

		public String getProductionCode() {
			return productionCode;
		}

		public void setProductionCode(String productionCode) {
			this.productionCode = productionCode;
		}

		public String getProductionName() {
			return productionName;
		}

		public void setProductionName(String productionName) {
			this.productionName = productionName;
		}

		public String getSpecification() {
			return specification;
		}

		public void setSpecification(String specification) {
			this.specification = specification;
		}

		public String getUnitName() {
			return unitName;
		}

		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}

		public String getCommodityCode() {
			return commodityCode;
		}

		public void setCommodityCode(String commodityCode) {
			this.commodityCode = commodityCode;
		}
	}

	public static class DetailTax {
		// 税率(例如，16%传0.16)
		private BigDecimal taxRate;// ": 0.13,
		// 是否享受税收优惠政策，默认值 false
		private String preferentialTax;// ": false,
		// 享受税收优惠政策内容
		private String taxPolicy;// ": "",
		// 税率标志 (空-非0税率；0-出口退税 1-免税 2-不征税 3-普通0税率)
		private String zeroTax;// : "",
		// 税编版本（例如：32.0）
		private String taxCodeVersion;// ": "33.0",

		public BigDecimal getTaxRate() {
			return taxRate;
		}

		public void setTaxRate(BigDecimal taxRate) {
			this.taxRate = taxRate;
		}

		public String getPreferentialTax() {
			return preferentialTax;
		}

		public void setPreferentialTax(String preferentialTax) {
			this.preferentialTax = preferentialTax;
		}

		public String getTaxPolicy() {
			return taxPolicy;
		}

		public void setTaxPolicy(String taxPolicy) {
			this.taxPolicy = taxPolicy;
		}

		public String getZeroTax() {
			return zeroTax;
		}

		public void setZeroTax(String zeroTax) {
			this.zeroTax = zeroTax;
		}

		public String getTaxCodeVersion() {
			return taxCodeVersion;
		}

		public void setTaxCodeVersion(String taxCodeVersion) {
			this.taxCodeVersion = taxCodeVersion;
		}

		public String getTaxCode() {
			return taxCode;
		}

		public void setTaxCode(String taxCode) {
			this.taxCode = taxCode;
		}

		private String taxCode;// ": ""
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}
