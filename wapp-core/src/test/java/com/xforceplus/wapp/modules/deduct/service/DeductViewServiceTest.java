package com.xforceplus.wapp.modules.deduct.service;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class DeductViewServiceTest extends BaseUnitTest {

    @Autowired
    private DeductViewService deductViewService;
    @Test
    public void testSummary() {
        final DeductListRequest deductListRequest = new DeductListRequest();

        final Map<String, Object> summary = deductViewService.summary(deductListRequest, XFDeductionBusinessTypeEnum.EPD_BILL);
        System.out.println("summary:"+ JSON.toJSONString(summary));
    }
}