package com.xforceplus.wapp.modules.backFill.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 云识别异步通知结果
 * https://wiki.xforceplus.com/pages/viewpage.action?pageId=5544015
 * @author zhaochao
 * @date 2021-9-15 20:52:43
 *
 */
@Data
public class DiscernResultDetail implements Serializable {

	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	/** 发票代码 */
	private String invoiceCode;
	/** 发票号码 */
	private String invoiceNo;
	/** 校验码 */
	private String checkCode;

	/** 发票日期 */
	private String invoiceTime;
	/** 印刷发票代码 */
	private String invoiceCodeP;
	/** 印刷发票号码 */
	private String invoiceNoP;

	/**
	 * 联次 1：发票联； 2：抵扣联； 3：存根联； 4：第四联； 5：第五联； 6：第六联；
	 */
	private Integer sheetIndex;
	/** 密文 */
	private List<String> cipherList;

	/** 购方名称 */
	private String purchaserName;
	/** 购方税号 */
	private String purchaserTaxNo;
	/** 购方地址 */
	private String purchaserAddr;
	/** 购方电话 */
	private String purchaserTel;
	/** 购方开户行 */
	private String purchaserBank;
	/** 购方开户行账号 */
	private String purchaserBankNo;

	/** 税额 */
	private BigDecimal totalTax;
	/** 不含税金额 */
	private BigDecimal totalAmount;
	/** 含税金额 */
	private BigDecimal totalAmountTaxNum;

	/** 机器码 */
	private String machineCode;
	/** 文档类型 */
	private String documentType;
	/**
	 * 发票类型 //电子普通发票 ce, //增值税普通发票 c, //通行费 ct, //专用发票 s, //机动车 j, //卷票 cj, //货运专票
	 * j；
	 */
	private String invoiceType;

	/** 销方名称 */
	private String sellerName;
	/** 销方税号 */
	private String sellerTaxNo;
	/** 销方地址 */
	private String sellerAddr;
	/** 销方电话 */
	private String sellerTel;
	/** 销方开户行 */
	private String sellerBank;
	/** 销方开户行账号 */
	private String ellerBankNo;

	/** 中文金额 */
	private String totalAmountTaxChineseText;
	/** 中文数字金额 */
	private BigDecimal totalAmountTaxChinese;
	/** 收款人 */
	private String payee;
	/** 复核 */
	private String recheck;
	/** 开票人 */
	private String drawer;
	/** 销售方（章） */
	private String stamper;
	/** 备注 */
	private String remark;
	/** 发票明细 */
	private List<Map<String,String>> invoiceDetails;
	
	}