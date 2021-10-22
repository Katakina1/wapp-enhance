package com.xforceplus.wapp.service;

import com.xforceplus.wapp.BaseUnitTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CommAgreementBillServiceTest extends BaseUnitTest {

    @Autowired
    private CommAgreementService commAgreementService;

    @Test
    public void testDestroyAgreementSettlement() {
      //  commAgreementService.destroyAgreementSettlement(1L);
    }

    @Test
    public void testAgainSplitPreInvoice() {
       // commAgreementService.againSplitPreInvoice(1L);
    }
}
