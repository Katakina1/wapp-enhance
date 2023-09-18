package com.xforceplus.wapp.handle;


import com.google.common.collect.Maps;
import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.converters.InvoiceConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
public class InvoiceHandlerTest extends BaseUnitTest {
    @Autowired
    private InvoiceHandler taxWareInvoiceHandler;

//    @SpyBean
//    private InvoiceConverter invoiceConverter;

    @Test
    @Rollback(value = false)
    public void testHandler() {
        String payloadString = "{\"data\":{\"sellerAddress\":\"\",\"sellerAddrTel\":\"深圳市龙岗区坂田街道坂田社区坂雪岗大道3010号3层310室0755-2265983\",\"invoiceType\":\"01-10-01\",\"purchaserCompanyId\":6214840512482746403,\"id\":523488341910654977,\"invoiceNo\":\"28430711\",\"purchaserInvoiceItemVOList\":[{\"unitPrice\":704.27,\"amountWithoutTax\":1408.54,\"itemSpec\":\"1*200\",\"quantity\":2,\"goodsTaxNo\":\"1060105990000000000\",\"invoiceExtend\":{\"goods_tax_no\":\"1060105990000000000\"},\"quantityUnit\":\"件\",\"updateTime\":\"2022-02-11 17:22:44.910\",\"invoiceCode\":\"4403213130\",\"hashValue\":\"\",\"businessExtend\":{\"vat2_ledger_purchaser\":{}},\"itemSequence\":1,\"taxRate\":\"0.13\",\"cargoName\":\"*纸制品*粘胶纸（离型式）\",\"createTime\":\"2022-02-11 17:22:35\",\"id\":523577393146007552,\"invoiceMainId\":523488341910654977,\"taxAmount\":183.11,\"amountWithTax\":1591.65,\"invoiceNo\":\"28430711\",\"generateChannel\":15,\"synchronizeTime\":\"2022-02-11 17:22:44.898\",\"channelSource\":15},{\"unitPrice\":451.28,\"amountWithoutTax\":2707.68,\"itemSpec\":\"1*2\",\"quantity\":6,\"goodsTaxNo\":\"1060512990000000000\",\"invoiceExtend\":{\"goods_tax_no\":\"1060512990000000000\"},\"quantityUnit\":\"箱\",\"updateTime\":\"2022-02-11 17:22:35.932\",\"invoiceCode\":\"4403213130\",\"hashValue\":\"\",\"itemSequence\":2,\"taxRate\":\"0.13\",\"cargoName\":\"*日用杂品*灭蝇灯\",\"createTime\":\"2022-02-11 17:22:35\",\"id\":523577393146007553,\"invoiceMainId\":523488341910654977,\"taxAmount\":352,\"amountWithTax\":3059.68,\"invoiceNo\":\"28430711\",\"generateChannel\":15,\"synchronizeTime\":\"2022-02-11 17:22:35.929\",\"channelSource\":15}],\"redFlag\":1,\"channelSource\":15,\"machineCode\":\"\",\"invoiceOrigin\":2,\"invoiceCode\":\"4403213130\",\"purchaserName\":\"沃尔玛（深圳）百货有限公司\",\"checkCode\":\"16203854426304158883\",\"taxCategory\":\"01\",\"purchaserBankName\":\"\",\"taxRate\":\"\",\"purchaserTenantId\":5793722992790675456,\"sellerTaxNo\":\"914403002793994177\",\"purchaserTaxNo\":\"914403006803670400\",\"paperDrewDate\":\"20220122\",\"checkerName\":\"刘明辉\",\"taxAmount\":535.11,\"sellerBankName\":\"\",\"generateChannel\":10,\"status\":1,\"industryIssueType\":\"10\",\"purchaserOrgId\":6214840512482746405,\"invoicerName\":\"苏小风\",\"authSyncStatus\":1,\"authTaxPeriod\":\"\",\"invoiceExtend\":{\"service_type\":\"AP3\",\"ledger_identifier\":\"1\",\"check_code\":\"16203854426304158883\",\"new_purchaser_tax_no\":\"914403006803670400\",\"auth_way\":\"\",\"purchaser_company_code\":\"57a0921b42284f94b51ae3bdf45d071a\",\"purchaser_tenant_ids\":\"#5793722992790675456#\",\"check_way\":\"\"},\"sellerName\":\"深圳市联亮实业发展有限公司\",\"authUse\":0,\"remark\":\"供应商号码：021462620\\n订单号：3050609449\\n店号：4830\\nJV代码：NF\\n公司代码：D087\",\"purchaserAddress\":\"\",\"cashierName\":\"张卫东\",\"effectiveTaxAmount\":535.11,\"amountWithoutTax\":4116.22,\"turnOutStatus\":0,\"sellerBankNameAccount\":\"民生银行深圳分行营业部697853729\",\"authStatus\":0,\"updateTime\":\"2022-02-11 17:22:35.926\",\"hashValue\":\"\",\"businessExtend\":{\"ledger_purchaser\":{\"elCheckTime\":\"\",\"elTime\":\"20220211112724856\",\"authStatus\":\"0\",\"pid\":\"52ff2b6d-d5b6-4217-aca0-a747ea1a194a\",\"elUserId\":\"914403006803670400\",\"authTime\":\"\",\"elStatus\":\"1\",\"elTaxPeriod\":\"\",\"elEnsureTime\":\"\",\"lastModifyTime\":\"20220211112844237\",\"authResult\":\"\",\"invoiceType\":\"s\",\"id\":\"\",\"effectiveTaxAmount\":\"535.110000\",\"elFlag\":\"0\",\"status\":\"1\",\"redFlag\":\"\"},\"vat2_ledger_purchaser\":{\"serviceType\":\"AP3\",\"sellerBankInfo\":\"民生银行深圳分行营业部697853729\",\"specialType\":\"\",\"isSaleList\":\"0\",\"invoiceType\":\"s\",\"id\":\"\",\"purchaserBankInfo\":\"中国工商银行深圳红围支行4000021209200314562\",\"status\":\"1\"}},\"invoiceMedium\":\"01\",\"createTime\":\"2022-02-11 11:28:44.464\",\"purchaserAddrTel\":\"深圳市龙岗区黄阁南路1号星河时代购物公园B1S-001、B2S-001、L1S-037、L2S-033  0755-28338000\",\"veriStatus\":0,\"purchaserBankNameAccount\":\"中国工商银行深圳红围支行4000021209200314562\",\"amountWithTax\":4651.33,\"synchronizeTime\":\"2022-02-11 17:22:35.915\",\"invoiceColor\":1},\"timestamp\":\"2022-02-11 17:22:45.456\"}";
        taxWareInvoiceHandler.handle(new SealedMessage(
                new SealedMessage.Header("111","purchaserInvoiceSync", Maps.newHashMap()),
                new SealedMessage.Payload(payloadString)));
//        ArgumentCaptor<Integer> arg2=ArgumentCaptor.forClass(Integer.class);
//        verify(invoiceConverter,times(1)).map(any(),arg2.capture());
//        Assertions.assertEquals(1,arg2.getValue());
    }

}