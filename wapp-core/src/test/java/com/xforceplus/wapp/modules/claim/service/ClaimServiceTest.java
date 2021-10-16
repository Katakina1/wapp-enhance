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

@Slf4j
public class ClaimServiceTest extends BaseUnitTest {

    @Autowired
    private DeductViewService claimService;

    @Test
    public void testApplyClaimVerdict() {
       // claimService.applyClaimVerdict();
    }

    @Test
    public void testRejectClaimVerdict() {
    }

    @Test
    public void testAgreeClaimVerdict() {
    }

    @Test
    public void testApplyClaimVerdictByBillDeductId() {
    }

    @Test
    public void testDeductByPage() {
        DeductListRequest request=new DeductListRequest();
        final PageResult<DeductListResponse> result = claimService.deductClaimByPage(request);

        System.out.println("r:"+JSON.toJSONString(result));
    }
}