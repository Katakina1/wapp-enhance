package com.xforceplus.wapp.service;

import com.xforceplus.wapp.BaseUnitTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CommPreInvoiceServiceTest extends BaseUnitTest {

    @Autowired
    private CommPreInvoiceService commPreInvoiceService;

    @Test
    public void testFillPreInvoiceClaimRedNotification() {
        //commPreInvoiceService.fillPreInvoiceClaimRedNotification(1L,"1111");
    }

    @Test
    public void testApplyPreInvoiceRedNotificationFail() {
       // commPreInvoiceService.applyPreInvoiceRedNotificationFail(1L);
    }
}
