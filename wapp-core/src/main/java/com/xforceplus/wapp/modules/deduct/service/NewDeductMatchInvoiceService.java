package com.xforceplus.wapp.modules.deduct.service;

import java.util.List;

import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;

/**
 * <pre>
 * 新业务单匹配
 * 需求来源：https://jira.xforceplus.com/browse/WALMART-2413
 * </pre>
 * @author just
 *
 */
public interface NewDeductMatchInvoiceService {

	/**
	 * <pre>
	 * 业务单匹配蓝票
	 * left、返回错误代码， T 表示返回成功，其他都失败
	 * 
	 * </pre>
	 * @param tXfBillDeductEntity
	 * @return
	 */
	R<List<BlueInvoiceService.InvoiceItem>> deductMatchInvoice(long decductId);
	
	/**
	 * <pre>
	 * 索赔业务单匹配蓝票
	 * left、返回错误代码， T 表示返回成功，其他都失败
	 * 
	 * </pre>
	 * @param tXfBillDeductEntity
	 * @return
	 */
	R<List<BlueInvoiceService.MatchRes>> deductMatchInvoiceByClaim(TXfBillDeductEntity tXfBillDeductEntity);
	
	/**
	 * <pre>
	 * 业务单匹配蓝票
	 * left、返回错误代码， T 表示返回成功，其他都失败
	 * 
	 * </pre>
	 * @param tXfBillDeductEntity
	 * @return
	 */
	R<List<BlueInvoiceService.InvoiceItem>> deductMatchInvoice(TXfBillDeductEntity tXfBillDeductEntity);
	
	/**
	 * <pre>
	 * 获取发票明细
	 * 1、重新计算正数明细行的折扣金额
	 * 2、过滤合计行
	 * 3、过滤负数折扣行
	 * </pre>
	 * @param uuid
	 * @return
	 */
	List<TDxRecordInvoiceDetailEntity> getInvoiceItems(String uuid);
}
