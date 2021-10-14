package com.xforceplus.wapp.service;

import com.xforceplus.wapp.WappApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WappApplication.class)
@Slf4j
public class CommSettlementServiceTest {

    @Autowired
    private CommSettlementService commSettlementService;

    @Test
    public void testApplyDestroySettlementPreInvoice() {
    }

    @Test
    public void testRejectDestroySettlementPreInvoice() {
    }

    @Test
    public void testAgreeDestroySettlementPreInvoice() {
    }

    @Test
    public void testAgainSplitSettlementPreInvoice() {
    }

    @Test
    public void testAgreeDestroySettlementPreInvoiceByPreInvoiceId() {
    }

    @Test
    public void testRejectDestroySettlementPreInvoiceByPreInvoiceId() {
    }
}
