package com.xforceplus.wapp.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.modules.deduct.service.NewDeductMatchInvoiceService;

public class Test_NewDeductMatchInvoiceService extends BaseUnitTest {

	@Autowired
	private NewDeductMatchInvoiceService newDeductMatchInvoiceService;
	
	@Test
	public void test_getInvoiceItems() {
		Object list = newDeductMatchInvoiceService.getInvoiceItems("440321413000109127");
		System.err.println(JSON.toJSONString(list));
	}
	
	
	@Test
	public void test_deductMatchInvoice() {
		Object list = newDeductMatchInvoiceService.deductMatchInvoice(238465081892683776L);
		System.err.println(JSON.toJSONString(list));
	}
	
	@Test
	public void test_deductMatchInvoice_Agreement() {
		Object list = newDeductMatchInvoiceService.deductMatchInvoice(238465081892683776L);
		System.err.println(JSON.toJSONString(list));
	}
	
}
