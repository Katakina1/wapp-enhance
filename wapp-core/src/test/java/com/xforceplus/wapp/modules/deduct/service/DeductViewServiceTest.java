package com.xforceplus.wapp.modules.deduct.service;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.epd.dto.SummaryResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DeductViewServiceTest extends BaseUnitTest {

    @Autowired
    private DeductViewService deductViewService;
    @Test
    public void testSummary() {
        final DeductListRequest deductListRequest = new DeductListRequest();

        final List<SummaryResponse> summary = deductViewService.summary(deductListRequest, XFDeductionBusinessTypeEnum.EPD_BILL);
        System.out.println("summary:"+ JSON.toJSONString(summary));
    }

    @Test
    public void testSumDueAndNegative() {
        final BigDecimal bigDecimal = deductViewService.sumDueAndNegative("PT", "172164", XFDeductionBusinessTypeEnum.EPD_BILL, new BigDecimal("0.09"));
        System.out.println("sumDueAndNegative---->"+bigDecimal.toPlainString());
    }
}