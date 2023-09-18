package com.xforceplus.wapp.modules.noneBusiness;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.modules.noneBusiness.dto.MtrIcSaveDto;
import com.xforceplus.wapp.modules.noneBusiness.service.MtrSaveService;

public class Test_MtrSaveService extends BaseUnitTest {

	@Autowired
	private MtrSaveService mtrSaveService;
	
	@Test
	public void test_saveMtrInfo() {
		String temp = "{\"batchNo\":\"283400004090009\",\"bussinessNo\":\"283400004090009\",\"bussinessType\":\"9\",\"createTime\":1667291182932,\"createUser\":\"3.0MTR\",\"invoiceRemark\":\"\",\"invoiceStoreNo\":\"3400\",\"invoiceType\":\"\",\"mtrIcInvoiceDetailDto\":[{\"detailAmount\":\"600000\",\"model\":\"\",\"num\":\"1\",\"taxAmount\":\"0\",\"taxRate\":\"0\",\"unit\":\"\",\"unitPrice\":\"600000\"},{\"detailAmount\":\"-6000\",\"model\":\"\",\"num\":\"0\",\"taxAmount\":\"0\",\"taxRate\":\"0\",\"unit\":\"\",\"unitPrice\":\"0\"}],\"mtrIcInvoiceMainDto\":{\"checkCode\":\"79809457063786317219\",\"companyCode\":\"EL\",\"gfAddressAndPhone\":\" \",\"gfBankAndNo\":\" \",\"gfName\":\"9144190068869154XN\",\"gfTaxNo\":\"\",\"invoiceAmount\":\"594000\",\"invoiceCode\":\"930013100650\",\"invoiceDate\":\"20221024\",\"invoiceNo\":\"39121567\",\"invoiceStatus\":\"0\",\"invoiceType\":\"10\",\"jv\":\"GJ 订单号[3400-2171-202204-283400004090009-13]\",\"paperDate\":\"20221024\",\"taxAmount\":\"0\",\"taxRate\":\"0\",\"totalAmount\":\"594000\",\"xfAddressAndPhone\":\"深圳市福田区农林路69号深国投广场二号楼2-5层及三号楼1-12层 0755-21512288\",\"xfBankAndNo\":\"中国工商银行深圳市红围支行 4000021219200065217\",\"xfName\":\"沃尔玛（中国）投资有限公司\",\"xfTaxNo\":\"914403007109368585\"},\"ofdStatus\":\"1\",\"reason\":\"3.0开具\",\"storeEnd\":\"\",\"storeNo\":\"2171\",\"storeStart\":\"\",\"verifyStatus\":\"1\",\"voucherNo\":\"\"}";
		MtrIcSaveDto mtrIcSaveDto = JSON.parseObject(temp, MtrIcSaveDto.class);
		Object result = mtrSaveService.saveMtrInfo(mtrIcSaveDto);
		System.out.println("结果：" + JSON.toJSONString(result));
	}
}
