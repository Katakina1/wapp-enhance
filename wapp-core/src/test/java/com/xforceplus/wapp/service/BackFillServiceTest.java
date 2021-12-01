package com.xforceplus.wapp.service;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.modules.backFill.model.VerificationBack;
import com.xforceplus.wapp.modules.backFill.service.EInvoiceMatchService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by SunShiyong on 2021/10/18.
 */
@Slf4j
public class BackFillServiceTest extends BaseUnitTest {

    @Autowired
    EInvoiceMatchService eInvoiceMatchService;
    @Test
    public void testAfterVerify() {
        String back = "{\"result\":{\"invoiceType\":\"s\",\"invoiceDetails\":[{\"cargoName\":\"*口腔清洁护理品*舒适达多效护理牙膏\",\"itemSpec\":\"\",\"quantityUnit\":\"件\",\"quantity\":\"4\",\"taxRate\":\"13\",\"zeroTax\":\" \",\"unitPrice\":\"26.36\",\"amountWithoutTax\":\"105.44\",\"taxAmount\":\"13.71\",\"amountWithTax\":\"119.15\"}],\"invoiceMain\":{\"taskId\":\"7b50a908-4cd6-4a52-833b-cd77627ed777\",\"invoiceCode\":\"3600203130\",\"invoiceNo\":\"01918001\",\"invoiceType\":\"s\",\"purchaserTaxNo\":\"91420000695306507H\",\"purchaserName\":\"沃尔玛（湖北）商业零售有限公司\",\"purchaserAddrTel\":\"武汉市汉阳区龙阳大道特6号摩尔城负一层 027-84459806\",\"purchaserBankInfo\":\"中国工商银行武汉市中山大道支行 3202002809200173865\",\"sellerTaxNo\":\"913610000564256577\",\"sellerName\":\"沃尔玛（江西）商业零售有限公司抚州赣东大道分店\",\"sellerAddrTel\":\"江西省抚州市临川区赣东大道345号 0794-8336638\",\"sellerBankInfo\":\"中国农业银行股份有限公司抚州分行14351101040019326\",\"paperDrewDate\":\"20210713\",\"amountWithoutTax\":\"105.44\",\"taxAmount\":\"13.71\",\"amountWithTax\":\"119.15\",\"checkCode\":\"77629346400987993606\",\"machineCode\":\"661521630225\",\"remark\":\"MTR#12097005240468/发出方:2097;FH/购入方:2461;PN 订单号[2097-2461-202105-12097005240468-13]\",\"status\":\"1\",\"redFlag\":\"0\",\"ctStatus\":\"\",\"cpyStatus\":\"0\",\"checkNumber\":\"5\",\"checkTime\":\"2021-11-08 18:18:45\",\"goodsListFlag\":\"0\",\"dqCode\":\"3600\",\"dqName\":\"江西\",\"ofdDownloadUrl\":\"\",\"pdfDownloadUrl\":\"\",\"ofdPreviewUrl\":\"\",\"ofdImageUrl\":\"\"}},\"code\":\"TXWRVC0001\",\"message\":\"查验成功\",\"taskId\":\"7b50a908-4cd6-4a52-833b-cd77627ed777\"}";
        VerificationBack verificationBack = JSONObject.parseObject(back, VerificationBack.class);
        eInvoiceMatchService.matchResultAfterVerify(verificationBack,null);
    }



}
