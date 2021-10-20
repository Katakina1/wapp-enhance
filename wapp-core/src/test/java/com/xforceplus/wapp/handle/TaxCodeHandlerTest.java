package com.xforceplus.wapp.handle;

import com.google.common.collect.Maps;
import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.wapp.BaseUnitTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TaxCodeHandlerTest extends BaseUnitTest {
    @Autowired
    private TaxCodeHandler taxCodeHandler;


    @Test
    public void handle() {
        String json = "{\"taxConvertCode\": \"733042\",\"tenantCode\": \"JKL\",\"tenantName\": \"京客隆商业集团股份有限公司\",\"goodsTaxNo\": \"1030107010100000000\",\"standardItemName\":\"猪、牛、羊、鸡、鸭、鹅鲜、冷、冻肉\",\"itemCode\": \"733042\",\"itemName\": \"猪白条1567890\",\"taxPre\": \"1\",\"taxPreCon\": \"免税\",\"zeroTax\": \"1\",\"largeCategoryName\": \"大类猪肉\",\"largeCategoryCode\": \"01\",\"medianCategoryName\": \"中类猪肉\",\"medianCategoryCode\": \"0101\",\"smallCategoryName\": \"小类猪肉\",\"smallCategoryCode\": \"010101\",\"status\": \"2\",\"taxRate\": \"0.1\"}";
        taxCodeHandler.handle(new SealedMessage(
                new SealedMessage.Header("111","taxcodeCooperation", Maps.newHashMap()),
                new SealedMessage.Payload(json)));
    }
}