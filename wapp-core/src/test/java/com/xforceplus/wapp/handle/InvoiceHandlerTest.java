package com.xforceplus.wapp.handle;


import com.google.common.collect.Maps;
import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.wapp.BaseUnitTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class InvoiceHandlerTest extends BaseUnitTest {
    @Autowired
    private InvoiceHandler taxWareInvoiceHandler;

    @Test
    public void testHandler() {
        String payloadString = "{\"authStyle\":0,\"purchaserBankAccount\":\"\",\"sellerAddress\":\"上海市浦东新区东川公路5979号 021-68918502\",\"cipherText\":\"11\",\"sellerAddrTel\":\"上海市浦东新区东川公路5979号 021-68918502\",\"veriResponseTime\":\"2020-07-08T09:28:57\",\"invoiceType\":\"02-10-01\",\"purchaserCompanyId\":4381736910926890000,\"id\":302767806951092200,\"invoiceNo\":\"97748894\",\"purchaserInvoiceItemVOList\":[{\"itemSpec\":\"\",\"discountRate\":0,\"goodsTaxNo\":\"\",\"cargoCode\":\"\",\"goodsErpNo\":\"\",\"taxItem\":\"\",\"quantityUnit\":\"公斤\",\"taxRateType\":0,\"itemSequence\":1,\"tollEndDate\":\"\",\"discountWithoutTax\":0,\"taxDedunction\":0,\"id\":1271256941004198000,\"invoiceNo\":\"97748894\",\"vehicleType\":\"\",\"channelSource\":4,\"unitPrice\":14.642201834862385,\"amountWithoutTax\":60.73,\"discountWithTax\":0,\"priceMethod\":\"\",\"quantity\":4.147869674185464,\"tollStartDate\":\"\",\"goodsNoVer\":\"\",\"taxPreFlag\":\"\",\"updateTime\":\"2020-06-12T09:44:08\",\"discountTax\":0,\"plateNumber\":\"\",\"invoiceCode\":\"031001800204\",\"hashValue\":\"\",\"businessExtend\":{},\"taxRate\":\"9\",\"cargoName\":\"*水果*苹果\",\"taxPreContent\":\"\",\"createTime\":\"2020-06-12T09:44:08\",\"discountFlag\":\"\",\"invoiceMainId\":302767806951092200,\"taxAmount\":5.47,\"amountWithTax\":66.2,\"generateChannel\":4,\"synchronizeTime\":\"2020-06-16T15:38:26\"}],\"redFlag\":1,\"channelSource\":4,\"machineCode\":\"661535786618\",\"invoiceOrigin\":2,\"turnOutAmount\":0,\"turnOutPeriod\":\"\",\"authResponseTime\":\"1970-01-01T12:00\",\"invoiceCode\":\"031001800204\",\"purchaserName\":\"上海云砺信息科技有限公司\",\"checkCode\":\"74407819110659682676\",\"taxCategory\":\"02\",\"purchaserBankName\":\"\",\"taxRate\":\"9%\",\"purchaserTenantId\":1250711864846532600,\"authBussiDate\":\"\",\"sellerTaxNo\":\"91310115060937655W\",\"checkTime\":\"1970-01-01T12:00\",\"authRequestTime\":\"1970-01-01T12:00\",\"purchaserTaxNo\":\"91310113342290888U\",\"veriRequestTime\":\"2020-07-08T09:28:55\",\"paperDrewDate\":\"2019-07-06T00:00\",\"checkerName\":\"奥宇\",\"taxAmount\":5.47,\"sellerBankName\":\"\",\"generateChannel\":4,\"status\":1,\"industryIssueType\":\"10\",\"purchaserOrgId\":1154232610632581000,\"invoicerName\":\"王婷\",\"turnOutType\":0,\"authSyncStatus\":1,\"authTaxPeriod\":\"\",\"sellerName\":\"上海一心玛特超市有限公司\",\"authUse\":0,\"remark\":\"\",\"sellerCompanyId\":0,\"purchaserAddress\":\"\",\"redNotificationNo\":\"\",\"sellerTel\":\"\",\"authSyncTime\":\"1970-01-01T12:00\",\"cashierName\":\":孟焕\",\"effectiveTaxAmount\":0,\"amountWithoutTax\":60.73,\"sellerOrgId\":0,\"turnOutStatus\":0,\"sellerBankNameAccount\":\"上海农商银行合庆支行32773718010079624\",\"authStatus\":0,\"updateTime\":\"2020-07-08T10:56:24\",\"purchaserTel\":\"\",\"hashValue\":\"\",\"businessExtend\":{},\"invoiceMedium\":\"01\",\"createTime\":\"2020-06-12T09:44:08\",\"purchaserAddrTel\":\"\",\"sellerBankAccount\":\"\",\"veriStatus\":2,\"purchaserBankNameAccount\":\"上海农商银行合庆支行32773718010079624\",\"amountWithTax\":66.2,\"synchronizeTime\":\"2020-07-08T10:56:24\",\"invoiceColor\":1}";
        taxWareInvoiceHandler.handle(new SealedMessage(
                new SealedMessage.Header("111","purchaserInvoiceSync", Maps.newHashMap()),
                new SealedMessage.Payload(payloadString)));
    }

}