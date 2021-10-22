package com.xforceplus.wapp.service;

import com.xforceplus.wapp.BaseUnitTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CommClaimBillServiceTest extends BaseUnitTest {

    @Autowired
    private CommClaimService commClaimService;

    @Test
    public void testDestroyClaimSettlement() {
       // commClaimService.destroyClaimSettlement(1L);
    }

    @Test
    public void testSplitPreInvoice() {
        // commClaimService.splitPreInvoice(1L);
    }
}
