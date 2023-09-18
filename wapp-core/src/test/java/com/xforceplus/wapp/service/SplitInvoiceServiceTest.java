package com.xforceplus.wapp.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xforceplus.phoenix.split.model.CreatePreInvoiceParam;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import com.xforceplus.wapp.util.RestTemplateSingle;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Slf4j
public class SplitInvoiceServiceTest extends BaseUnitTest {

    @Mock
    private CommPreInvoiceService commPreInvoiceService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 集成平台地址
     */
    @Value("${wapp.integration.host.http}")
    private String janusPath;
    @Value("${wapp.integration.tenant-id:1203939049971830784}")
    public String tenantId;
    @Value("${wapp.integration.action.splitInvoice}")
    private String splitInvoice;
    @Value("${wapp.integration.sign.splitInvoice}")
    private String sign;
    @Value("${wapp.integration.authentication}")
    private String authentication;

    private static final long ONE_MB_SIZE = 200 * 1024L;

    //调用拆票服务功能测试
    @Test
    public void testSplitInvoice() throws InterruptedException {
        TXfSettlementEntity tXfSettlementEntity = new TXfSettlementEntity();
        tXfSettlementEntity.setId(200718385188454400L);
        tXfSettlementEntity.setSettlementNo("XY20230421tQpM");

        String param = "{\"billInfo\":{\"amountWithTax\":-5237.5400,\"amountWithoutTax\":-4805.0800,\"billItems\":[{\"amountWithTax\":-5237.540000,\"amountWithoutTax\":-4805.080000,\"deductions\":0,\"discountTax\":0,\"discountWithTax\":0,\"discountWithoutTax\":0,\"goodsNoVer\":\"47.0\",\"goodsTaxNo\":\"1030204010000000000\",\"innerDiscountTax\":0,\"innerDiscountWithTax\":0,\"innerDiscountWithoutTax\":0,\"innerPrepayAmountTax\":0,\"innerPrepayAmountWithTax\":0,\"innerPrepayAmountWithoutTax\":0,\"itemCode\":\"1030204010000000000\",\"itemName\":\"1*10*250ml金典纯牛奶(梦幻盖)\",\"itemShortName\":\"乳制品\",\"itemSpec\":\"250ml*10\",\"origin\":0,\"outterDiscountTax\":0,\"outterDiscountWithTax\":0,\"outterDiscountWithoutTax\":0,\"outterPrepayAmountTax\":0,\"outterPrepayAmountWithTax\":0,\"outterPrepayAmountWithoutTax\":0,\"quantity\":-111.668657535010621,\"quantityUnit\":\"提\",\"redItem\":false,\"remark\":\"\",\"salesbillId\":\"200718385188454400\",\"salesbillItemId\":\"200718385423335424\",\"salesbillItemNo\":\"\",\"salesbillNo\":\"XY20230421tQpM\",\"splitItem\":false,\"taxAmount\":-432.460000,\"taxPre\":\"\",\"taxPreCon\":\"\",\"taxRate\":0.09,\"unitPrice\":43.029800000000000,\"zeroTax\":\"\"}],\"invoiceType\":\"s\",\"priceMethod\":1,\"purchaserAddress\":\"深圳市福田区农林路69号深国投广场二号楼2-5层及三号楼1-12层\",\"purchaserBankAccount\":\"4000021219200065217\",\"purchaserBankName\":\"中国工商银行深圳市红围支行\",\"purchaserGroupId\":0,\"purchaserId\":0,\"purchaserName\":\"沃尔玛（中国）投资有限公司\",\"purchaserNo\":\"WI\",\"purchaserTaxNo\":\"914403007109368585\",\"purchaserTel\":\"0755-21512288\",\"purchaserTenantId\":0,\"remark\":\"\",\"salesbillId\":\"200718385188454400\",\"sellerAddress\":\"\",\"sellerBankAccount\":\"813180526910001\",\"sellerBankName\":\"招商银行总行营业部\",\"sellerGroupId\":0,\"sellerId\":0,\"sellerName\":\"深圳市粤鹏孚化工有限公司\",\"sellerNo\":\"341753\",\"sellerTaxNo\":\"91440300708444854L\",\"sellerTel\":\"\",\"sellerTenantId\":0,\"taxAmount\":-432.46},\"routingKey\":\"12\",\"rule\":{\"amountSplitRule\":\"2\",\"cargoNameLength\":92,\"customRemarkSize\":200,\"ignoreAllowableError\":false,\"invoiceItemMaxRow\":8,\"invoiceLimit\":9999.99,\"invoiceMaxErrorAmount\":0.05,\"itemSort\":\"2\",\"itemSpecNameLength\":36,\"limitIsAmountWithTax\":false,\"mergeBySplitFiled\":false,\"priceMethod\":\"WITHOUT_TAX\",\"remarkDuplicateFlag\":true,\"remarkFiledMetadataBeanList\":[{\"fieldDisplayName\":\"备注\",\"fieldGroupIndex\":\"BILL_INFO\",\"fieldName\":\"remark\"}],\"ruleId\":687162585176363000,\"saleListOption\":\"1\",\"salesListMaxRow\":2000,\"showSpecification\":true,\"splitByItemPriceQuantityNon\":false,\"splitFiledList\":[\"taxRate\"],\"unitPriceAmountOps\":\"0\",\"unitPriceScale\":15,\"zeroTaxOption\":\"NOT_PROCESS\"}}";
        JSONObject jsonObject = JSONObject.parseObject(param);
        CreatePreInvoiceParam createPreInvoiceParam = JSONObject.toJavaObject(jsonObject,CreatePreInvoiceParam.class);
        TAcOrgEntity tAcOrgEntity = new TAcOrgEntity();
        tAcOrgEntity.setTaxDeviceType(3);
//        preinvoiceService.doSplit(createPreInvoiceParam,tXfSettlementEntity,tAcOrgEntity);

        String post = "";
        JSONObject res = null;
        for(int i = 0;i<2;i++){
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.add("tenantId", tenantId);
                headers.add("Authentication", authentication);
                headers.add("accept", "application/json");
                headers.add("Accept-Encoding", "deflate");
                headers.add("Content-Type", "application/json");
                headers.add("x-userinfo", "tenant-gateway.41-tcenter-prod");
                headers.add("uiaSign", sign);
                headers.add("action", splitInvoice);
                headers.add("serialNo", tXfSettlementEntity.getSettlementNo());
                headers.add("rpcType", "http");
                headers.add("appId", "walmart");
                headers.add("taxDeviceType", "3");
                // headers.add("Content-Encoding", "gzip");
                log.info("tenantId:{},Authentication:{},uiaSign:{},action:{}",tenantId,authentication,sign,splitInvoice);
                HttpEntity<CreatePreInvoiceParam> entity = new HttpEntity<>(createPreInvoiceParam, headers);
                ResponseEntity<String> rs = null;
                String paramstr = JSON.toJSONString(createPreInvoiceParam);
                log.info("结算单[{}]拆票报文入参:[{}]", tXfSettlementEntity.getSettlementNo(), paramstr);
                log.info("结算单[{}]拆票报文大小(length:{})(byte:{})", tXfSettlementEntity.getSettlementNo(), paramstr.length(), paramstr.getBytes(StandardCharsets.UTF_8).length);
                //大于1M gzip压缩
                if (paramstr.getBytes(StandardCharsets.UTF_8).length > ONE_MB_SIZE) {
                    log.info("@@结算单ID:{},SettlementNo:{},body len:{}", tXfSettlementEntity.getId(), tXfSettlementEntity.getSettlementNo(), paramstr.length() / 1024);
                    rs = RestTemplateSingle.getGzipInstance().postForEntity(janusPath, entity, String.class);
                } else {
                    rs = restTemplate.postForEntity(janusPath, entity, String.class);
                }
                post = rs.getBody();
                res = JSONObject.parseObject(post);
                log.info("@@结算单ID:{} ,SettlementNo:{},拆票出参:{}", tXfSettlementEntity.getId(), tXfSettlementEntity.getSettlementNo(), post);
                if (!"BSCTZZ0001".equals(res.get("code")) || "[]".equals(res.get("result"))) {
                    log.error("结算单：{} 拆票失败，结果：{}", tXfSettlementEntity.getSettlementNo(), post);
                    // 保存拆票失败原因
                    String desc = "拆票失败+" + res.get("message");
                    throw new RuntimeException(desc);
                }
                //正常请求一次跳出循环
                break;
            } catch (RestClientResponseException e) {
                res = JSON.parseObject(e.getResponseBodyAsString());
                if (!"BSCTZZ0001".equals(res.get("code")) || "[]".equals(res.get("result"))) {
                    log.error("结算单：{} 拆票失败，结果：{}", tXfSettlementEntity.getSettlementNo(), post);
                    String desc = "拆票失败+" + res.get("message");
                    throw new RuntimeException(desc);
                }
            } catch (Exception e) {
                log.error("结算单：{} 拆票失败，结果：{}", tXfSettlementEntity.getSettlementNo(), post);
                String desc = "拆票失败+" + e.getMessage();
                if(i!=1){
                    Thread.sleep(1000L);
                    log.info("调用拆票失败，进行二次重试,结算单ID:{} ,SettlementNo:{}",tXfSettlementEntity.getId(), tXfSettlementEntity.getSettlementNo());
                    continue;
                }
                throw new RuntimeException(desc);
            }
        }
    }

}
