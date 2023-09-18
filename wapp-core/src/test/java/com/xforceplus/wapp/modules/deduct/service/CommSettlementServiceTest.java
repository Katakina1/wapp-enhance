package com.xforceplus.wapp.modules.deduct.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.service.CommSettlementService;

public class CommSettlementServiceTest extends BaseUnitTest {

	@Autowired
	private CommSettlementService commSettlementService;

	@Test
	public void testAgainSplitSettlementPreInvoice() {
		commSettlementService.againSplitSettlementPreInvoice(77149336157020160L);
		// Mockito.verify(commSettlementService,
		// Mockito.times(1)).againSplitSettlementPreInvoice(77149336157020160L);
	}
}
