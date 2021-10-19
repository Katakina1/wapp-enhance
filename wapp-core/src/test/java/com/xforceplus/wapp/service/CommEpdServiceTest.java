package com.xforceplus.wapp.service;

import com.xforceplus.wapp.BaseUnitTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CommEpdServiceTest extends BaseUnitTest {

    @Autowired
    private CommEpdService commEpdService;

    @Test
    public void testDestroyEpdSettlement() {
       // commEpdService.destroyEpdSettlement(1L);
    }

    @Test
    public void testAgainSplitPreInvoice() {
       // commEpdService.againSplitPreInvoice(1L);
    }
}