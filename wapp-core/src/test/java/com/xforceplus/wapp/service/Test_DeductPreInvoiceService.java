package com.xforceplus.wapp.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.common.enums.DeductRedNotificationEventEnum;
import com.xforceplus.wapp.common.event.DeductRedNotificationEvent;
import com.xforceplus.wapp.common.event.DeductRedNotificationEvent.DeductRedNotificationModel;
import com.xforceplus.wapp.modules.deduct.service.DeductPreInvoiceService;

public class Test_DeductPreInvoiceService extends BaseUnitTest {

	@Autowired
	private DeductPreInvoiceService deductPreInvoiceService;
	
	@Test
	public void test_consume_PRE_INVOICE_CREATED() {
		DeductRedNotificationEvent event = new DeductRedNotificationEvent();
		
		DeductRedNotificationModel deductRedNotificationModel = new DeductRedNotificationModel();
		deductRedNotificationModel.setPreInvoiceId(246429605438070784L);
		deductRedNotificationModel.setApplyRequired(Boolean.TRUE);
		
		event.setEvent(DeductRedNotificationEventEnum.PRE_INVOICE_CREATED);
		event.setTimestamp(1692774327117L);
		event.setBody(deductRedNotificationModel);
		deductPreInvoiceService.consume(event);
	}
}
