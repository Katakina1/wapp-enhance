package com.xforceplus.wapp.modules.deduct.model.threadlocal;

import com.alibaba.fastjson.JSON;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.xforceplus.wapp.modules.deduct.service.BlueInvoiceService;
import com.xforceplus.wapp.modules.deduct.service.BlueInvoiceService.InvoiceItem;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 线程池之间数据共享
 * @author just
 *
 */
public class BlueInvoiceMatchHolder {
	private static TransmittableThreadLocal<Map<String, BlueInvoiceService.MatchRes>> contextThreadLocal = new TransmittableThreadLocal<>();

	public static boolean available() {
		return contextThreadLocal.get() != null;
	}

	public static void clearContext() {
		contextThreadLocal.remove();
	}

	/**
	 * 填充明细值
	 * @param invoiceMatchResMap
	 */
	public static void put(Map<String, BlueInvoiceService.MatchRes> invoiceMatchResMap) {
		contextThreadLocal.set(invoiceMatchResMap);
	}

	public static Map<String, BlueInvoiceService.MatchRes> get() {
		return contextThreadLocal.get();
	}

	/**
	 * 获取已匹配的金额
	 * @param item
	 * @return
	 */
	public static BigDecimal getMatchedDetailAmount(TDxRecordInvoiceDetailEntity item) {
		BlueInvoiceService.InvoiceItem invoiceItem = getInvoiceItem(item);
		return invoiceItem == null ? null : invoiceItem.getMatchedDetailAmount();
	}

	public static BigDecimal getMatchedDetailQuantity(TDxRecordInvoiceDetailEntity item) {
		BlueInvoiceService.InvoiceItem invoiceItem = getInvoiceItem(item);
		return invoiceItem == null ? null : invoiceItem.getMatchedNum();
	}

	private static BlueInvoiceService.InvoiceItem getInvoiceItem(TDxRecordInvoiceDetailEntity item) {
		Map<String, BlueInvoiceService.MatchRes> matchResMap = get();
		if (matchResMap != null) {
			BlueInvoiceService.MatchRes matchRes = matchResMap.get(item.getUuid());
			if (matchRes == null) {
				return null;
			}
			List<BlueInvoiceService.InvoiceItem> invoiceItems = matchRes.getInvoiceItems();
			if (CollectionUtils.isEmpty(invoiceItems)) {
				return null;
			}
			//合并已匹配明细的已匹配金额和已匹配数量
			Map<Long, List<BlueInvoiceService.InvoiceItem>> map = invoiceItems.stream().collect(Collectors.groupingBy(InvoiceItem::getItemId));
			if(map.containsKey(item.getId())) {
				List<BlueInvoiceService.InvoiceItem> list = map.get(item.getId());
				BigDecimal matchedDetailAmount = BigDecimal.ZERO; //已匹配不含税金额
				BigDecimal matchedNum = BigDecimal.ZERO; //已匹配数量
				BigDecimal matchedTaxAmount = BigDecimal.ZERO; //已匹配税额
				BigDecimal leftDetailAmount = null;//明细不含税
				BigDecimal leftNum = null;//明细数量
				for (BlueInvoiceService.InvoiceItem baseInvoiceItem : list) {
					// 执行明细匹配金额合并
					matchedDetailAmount = matchedDetailAmount.add(baseInvoiceItem.getMatchedDetailAmount());
					if (leftDetailAmount == null || leftDetailAmount.compareTo(baseInvoiceItem.getLeftDetailAmount()) < 0) {// 剩余金额取最小
						leftDetailAmount = baseInvoiceItem.getLeftDetailAmount();
					}

					// 计算占用税额
					matchedTaxAmount = matchedTaxAmount.add(baseInvoiceItem.getMatchedTaxAmount());
					// 计算占用数量和单价
					if (baseInvoiceItem.getMatchedNum() != null) {
						matchedNum = matchedNum.add(baseInvoiceItem.getMatchedNum());
						if (leftNum == null || leftNum.compareTo(baseInvoiceItem.getLeftNum()) < 0) {// 剩余数量取最小
							leftNum = baseInvoiceItem.getLeftNum();
						}
						// 计算占用单价
						baseInvoiceItem.setMatchedUnitPrice(baseInvoiceItem.getMatchedDetailAmount().divide(baseInvoiceItem.getMatchedNum(), 15, RoundingMode.HALF_UP));
						BigDecimal originUnitPrice = new BigDecimal(baseInvoiceItem.getUnitPrice());
						if (baseInvoiceItem.getMatchedUnitPrice().compareTo(originUnitPrice) > 0) {
							// 如果匹配单价超过原始单价，则单价使用原始单价
							baseInvoiceItem.setMatchedUnitPrice(originUnitPrice);
						}
					}
				}
				//新构建对象，防止数据被用
				BlueInvoiceService.InvoiceItem resultItem = JSON.parseObject(JSON.toJSONString(list.get(0)), BlueInvoiceService.InvoiceItem.class);
				resultItem.setMatchedDetailAmount(matchedDetailAmount);
				resultItem.setMatchedNum(matchedNum);
				resultItem.setMatchedTaxAmount(matchedTaxAmount);
				return resultItem;
			}
			
		}
		return null;
	}
}
