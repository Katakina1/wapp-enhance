package com.xforceplus.wapp.service;

import com.xforceplus.wapp.BaseUnitTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;


@Slf4j
public class CommSettlementServiceTest extends BaseUnitTest {

    @Mock
    private CommSettlementService commSettlementService;

    @Test
    public void testApplyDestroySettlementPreInvoice() {
        commSettlementService.applyDestroySettlementPreInvoice(1417992416006172L, "备注");
        Mockito.verify(commSettlementService, Mockito.times(1)).applyDestroySettlementPreInvoice(1417992416006172L, "备注");
    }

    @Test
    public void testRejectDestroySettlementPreInvoice() {
        commSettlementService.rejectDestroySettlementPreInvoice(1417992416006172L, "备注");
        Mockito.verify(commSettlementService, Mockito.times(1)).rejectDestroySettlementPreInvoice(1417992416006172L, "备注");
    }

    @Test
    public void testAgreeDestroySettlementPreInvoice() {
        commSettlementService.agreeDestroySettlementPreInvoice(1417992416006172L);
        Mockito.verify(commSettlementService, Mockito.times(1)).agreeDestroySettlementPreInvoice(1417992416006172L);
    }

    @Test
    public void testAgainSplitSettlementPreInvoice() {
        commSettlementService.againSplitSettlementPreInvoice(77149336157020160L);
        //Mockito.verify(commSettlementService, Mockito.times(1)).againSplitSettlementPreInvoice(77149336157020160L);
    }

    @Test
    public void testAgreeDestroySettlementPreInvoiceByPreInvoiceId() {
        commSettlementService.agreeDestroySettlementPreInvoiceByPreInvoiceId(Arrays.asList(1L));
        Mockito.verify(commSettlementService, Mockito.times(1)).agreeDestroySettlementPreInvoiceByPreInvoiceId(Arrays.asList(1L));
    }

    @Test
    public void testRejectDestroySettlementPreInvoiceByPreInvoiceId() {
        commSettlementService.rejectDestroySettlementPreInvoiceByPreInvoiceId(Arrays.asList(1L), "备注");
        Mockito.verify(commSettlementService, Mockito.times(1)).rejectDestroySettlementPreInvoiceByPreInvoiceId(Arrays.asList(1L), "备注");
    }

    @Test
    public void testDestroyPreInvoice() {
        commSettlementService.destroyPreInvoice(1L);
        Mockito.verify(commSettlementService, Mockito.times(1)).destroyPreInvoice(1L);
    }

    @Test
    public void testApplyDestroyPreInvoiceAndRedNotification() {
        commSettlementService.applyDestroyPreInvoiceAndRedNotification(1L, "备注");
        Mockito.verify(commSettlementService, Mockito.times(1)).applyDestroyPreInvoiceAndRedNotification(1L, "备注");
    }

    @Test
    public void testCheckAgainSplitSettlementPreInvoice() {
        commSettlementService.checkAgainSplitSettlementPreInvoice(1L);
        Mockito.verify(commSettlementService, Mockito.times(1)).checkAgainSplitSettlementPreInvoice(1L);
    }

}
