package com.xforceplus.wapp.service;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.BaseUnitTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Created by SunShiyong on 2021/10/18.
 */
@Slf4j

public class BackFillServiceTest extends BaseUnitTest {

    @Autowired
    com.xforceplus.wapp.modules.backfill.service.EInvoiceMatchService eInvoiceMatchService;
    @Test
    public void testAfterVerify() {
        String back = "{\"result\":{\"invoiceType\":\"se\",\"invoiceDetails\":[{\"cargoName\":\"*供电*电费\",\"itemSpec\":\"C\",\"quantityUnit\":\"\",\"quantity\":\"81705\",\"taxRate\":\"13\",\"zeroTax\":\" \",\"unitPrice\":\"0.6139336638\",\"amountWithoutTax\":\"50161.45\",\"taxAmount\":\"6520.99\",\"amountWithTax\":\"56682.44\"}],\"invoiceMain\":{\"taskId\":\"eb6e2b5a-4141-42ed-a5a2-44d85f37a39a\",\"invoiceCode\":\"033002000113\",\"invoiceNo\":\"19498486\",\"invoiceType\":\"se\",\"purchaserTaxNo\":\"91330000681660329U\",\"purchaserName\":\"沃尔玛(浙江)百货有限公司\",\"purchaserAddrTel\":\"浙江省杭州市西湖区古墩路588号三楼-10571-89871600\",\"purchaserBankInfo\":\"中国工商银行杭州拱宸支行1202020819900107803\",\"sellerTaxNo\":\"91330185MA2KJU9E6G\",\"sellerName\":\"国网浙江省电力有限公司杭州市临安区供电公司\",\"sellerAddrTel\":\"浙江省杭州市临安区锦城街道万马路269号0571-51235112\",\"sellerBankInfo\":\"中国建设银行股份有限公司杭州临安支行营业部33050161732709625807\",\"paperDrewDate\":\"20211206\",\"amountWithoutTax\":\"50161.45\",\"taxAmount\":\"6520.99\",\"amountWithTax\":\"56682.44\",\"checkCode\":\"07242811599730608070\",\"machineCode\":\"667118974720\",\"remark\":\"户号:6320044314,年月:202111,地址:*锦城街道锦潭社区钱王街钱王街855号-2,\",\"status\":\"1\",\"redFlag\":\"0\",\"ctStatus\":\"\",\"cpyStatus\":\"0\",\"checkNumber\":\"10\",\"checkTime\":\"2022-01-28 13:35:33\",\"goodsListFlag\":\"0\",\"dqCode\":\"3300\",\"dqName\":\"浙江\",\"ofdDownloadUrl\":\"https://fpjr.zhejiang.chinatax.gov.cn:9999/api?action=getDoc&code=033002000113_19498486_20211206_E0D3B1C6&type=3\",\"pdfDownloadUrl\":\"https://fpjr.zhejiang.chinatax.gov.cn:9999/api?action=getDoc&code=033002000113_19498486_20211206_E0D3B1C6&type=12\",\"ofdPreviewUrl\":\"https://fpjr.zhejiang.chinatax.gov.cn:9999/web-reader/reader?file=033002000113_19498486_20211206_E0D3B1C6\",\"ofdImageUrl\":\"https://fpjr.zhejiang.chinatax.gov.cn:9999/web-reader/reader/image?_b=3.2.0&_d=033002000113_19498486_20211206_E0D3B1C6&_i=0&_v=0&_p=192\"}},\"code\":\"TXWRVC0001\",\"message\":\"查验成功\",\"taskId\":\"eb6e2b5a-4141-42ed-a5a2-44d85f37a39a\"}";
        com.xforceplus.wapp.modules.backfill.model.VerificationBack verificationBack = JSONObject.parseObject(back, com.xforceplus.wapp.modules.backfill.model.VerificationBack.class);
        eInvoiceMatchService.matchResultAfterVerify(verificationBack,null);
    }



}
