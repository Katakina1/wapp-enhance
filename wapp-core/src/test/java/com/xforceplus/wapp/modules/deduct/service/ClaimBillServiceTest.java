package com.xforceplus.wapp.modules.deduct.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.modules.statement.service.StatementServiceImpl;

public class ClaimBillServiceTest extends BaseUnitTest {

	@Autowired
	private ClaimBillService claimBillService;
	@Autowired
	private StatementServiceImpl statementServiceImpl;
	
	@Test
	public void test_matchClaimBill() {
		boolean result = claimBillService.matchClaimBill();
		System.err.println("test_matchClaimBill:" + result);
	}
	
	@Test
	public void test_mergeClaimSettlement() {
		boolean result = claimBillService.mergeClaimSettlement();
		System.err.println("test_matchClaimBill:" + result);
	}
	
	@Test
	public void test_baseInformationClaimPage() {
		Object obj = statementServiceImpl.baseInformationClaimPage(1L, 10L, "SP20220511bKkt");
		System.err.println("test_baseInformationClaimPage:" + JSON.toJSONString(obj));
	}
}
