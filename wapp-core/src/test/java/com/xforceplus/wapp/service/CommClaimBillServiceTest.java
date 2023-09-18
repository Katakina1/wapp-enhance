package com.xforceplus.wapp.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.xforceplus.wapp.BaseUnitTest;

public class CommClaimBillServiceTest extends BaseUnitTest {

	@Autowired
    private CommClaimService commClaimService;

    @Test
    public void testDestroyClaimSettlement() {
        commClaimService.destroyClaimSettlement(108038736251535360L);
        //Mockito.verify(commClaimService, Mockito.times(1)).destroyClaimSettlement(43181899665424384L);
    }

}
