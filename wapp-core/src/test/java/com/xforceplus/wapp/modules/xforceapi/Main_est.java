package com.xforceplus.wapp.modules.xforceapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;

public class Main_est {

	public static void main(String[] args) {
		List<TXfPreInvoiceEntity> resultList = new ArrayList<TXfPreInvoiceEntity>();
		resultList.add(create("111111", "222"));
		resultList.add(create("111111", "2222"));
		Map<String, Set<String>> deductSettlementNo2RedNotificationNoMap = resultList.stream().collect(Collectors.groupingBy(TXfPreInvoiceEntity::getSettlementNo,
                Collectors.mapping(TXfPreInvoiceEntity::getRedNotificationNo, Collectors.toSet())));
		
		
		System.err.println(JSON.toJSONString(deductSettlementNo2RedNotificationNoMap));
		System.err.println(Joiner.on(",").join(deductSettlementNo2RedNotificationNoMap.get("111111")));
	}
	
	public static TXfPreInvoiceEntity create(String settlementNo, String redNotificationNo) {
		TXfPreInvoiceEntity entity = new TXfPreInvoiceEntity();
		entity.setSettlementNo(settlementNo);
		entity.setRedNotificationNo(redNotificationNo);
		return entity;
	}
}
