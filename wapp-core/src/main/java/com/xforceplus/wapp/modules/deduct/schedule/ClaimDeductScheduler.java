package com.xforceplus.wapp.modules.deduct.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.modules.deduct.service.ClaimBillService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ClaimDeductScheduler {

    @Autowired
    private ClaimBillService claimBillService;
    public static String KEY = "Claim-match";
    @Autowired
    private LockClient lockClient;

    /**
     * 索赔单匹配
     */
    @Async("taskThreadPoolExecutor")
    @Scheduled(cron = "${task.ClaimDeductScheduler-cron}")
    public void claimDeductDeal() {
        lockClient.tryLock(KEY, () -> {
            log.info("Claim-match job  开始");
            try {
                claimBillService.matchClaimBill();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            log.info("Claim-match job  结束");
        }, -1, 1);
    }

}

