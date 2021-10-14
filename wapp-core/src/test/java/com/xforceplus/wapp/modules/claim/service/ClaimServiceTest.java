package com.xforceplus.wapp.modules.claim.service;

import com.xforceplus.wapp.WappApplication;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WappApplication.class)
@Slf4j
public class ClaimServiceTest {

    @Autowired
    private ClaimService claimService;


    @Test
    public void testApplyClaimVerdict() {
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
}