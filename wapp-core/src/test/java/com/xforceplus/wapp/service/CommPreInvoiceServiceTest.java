package com.xforceplus.wapp.service;

import com.xforceplus.wapp.BaseUnitTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

@Slf4j
public class CommPreInvoiceServiceTest extends BaseUnitTest {

    @Mock
    private CommPreInvoiceService commPreInvoiceService;

    @Test
    public void testFillPreInvoiceClaimRedNotification() {
        commPreInvoiceService.fillPreInvoiceRedNotification(1L, "1111");
        Mockito.verify(commPreInvoiceService, Mockito.times(1)).fillPreInvoiceRedNotification(1L, "1111");
    }

    @Test
    public void testApplyPreInvoiceRedNotificationFail() {
        commPreInvoiceService.applyPreInvoiceRedNotificationFail(1L);
        Mockito.verify(commPreInvoiceService, Mockito.times(1)).applyPreInvoiceRedNotificationFail(1L);
    }
}
