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
public class CommClaimServiceTest {

    @Autowired
    private CommClaimService commClaimService;

    @Test
    public void testDestroyClaimSettlement() {
    }
}
