package com.xforceplus.wapp.service;

import com.xforceplus.wapp.BaseUnitTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


@Slf4j
public class CommSettlementServiceTest extends BaseUnitTest {

    @Autowired
    private CommSettlementService commSettlementService;

    @Test
    public void testApplyDestroySettlementPreInvoice() {
        //commSettlementService.applyDestroySettlementPreInvoice(1853061001646080L);
    }

    @Test
    public void testRejectDestroySettlementPreInvoice() {
        //commSettlementService.rejectDestroySettlementPreInvoice(1L);
    }

    @Test
    public void testAgreeDestroySettlementPreInvoice() {
        //commSettlementService.agreeDestroySettlementPreInvoice(1L);
    }

    @Test
    public void testAgainSplitSettlementPreInvoice() {
        //commSettlementService.againSplitSettlementPreInvoice(1L);
    }

    @Test
    public void testAgreeDestroySettlementPreInvoiceByPreInvoiceId() {
       // commSettlementService.agreeDestroySettlementPreInvoiceByPreInvoiceId(Arrays.asList(1L));
    }

    @Test
    public void testRejectDestroySettlementPreInvoiceByPreInvoiceId() {
        //commSettlementService.rejectDestroySettlementPreInvoiceByPreInvoiceId(Arrays.asList(1L));
    }

    @Test
    public void testDestroyPreInvoice(){
        // commSettlementService.destroyPreInvoice(1L);
    }

    @Test
    public void testApplyDestroyPreInvoiceAndRedNotification(){
       // commSettlementService.applyDestroyPreInvoiceAndRedNotification(1L);
    }

    @Test
    public void testCheckAgainSplitSettlementPreInvoice(){
      //  commSettlementService.checkAgainSplitSettlementPreInvoice(1L);
    }

}
