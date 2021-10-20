package com.xforceplus.wapp.modules.claim.service;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListResponse;
import com.xforceplus.wapp.modules.deduct.service.DeductViewService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

@Slf4j
public class ClaimServiceTest extends BaseUnitTest {

    @Autowired
    private ClaimService claimService;
    @Autowired
    private DeductViewService deductViewService;

    @Test
    public void testApplyClaimVerdict() {
        // claimService.applyClaimVerdict(1L);
    }

    @Test
    public void testRejectClaimVerdict() {
        //  claimService.rejectClaimVerdict(1L);
    }

    @Test
    public void testAgreeClaimVerdict() {
        //  claimService.agreeClaimVerdict(1L);
    }

    @Test
    public void testApplyClaimVerdictByBillDeductId() {
        // claimService.applyClaimVerdictByBillDeductId(Arrays.asList(1L));
    }

    @Test
    public void testDeductByPage() {
        DeductListRequest request = new DeductListRequest();
        final PageResult<DeductListResponse> result = deductViewService.deductClaimByPage(request);
        System.out.println("r:" + JSON.toJSONString(result));
    }
}