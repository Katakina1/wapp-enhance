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
public class ClaimSettlementScheduler {

    @Autowired
    private ClaimBillService claimBillService;
    @Autowired
    private LockClient lockClient;
    public static String KEY = "claim-MergeSettlement";

    /**
     * 索赔单生成结算单
     */
    @Async("taskThreadPoolExecutor")
    @Scheduled(cron="${task.ClaimSettlementScheduler-cron}")
    public void claimMergeSettlementDeductDeal() {
        lockClient.tryLock(KEY, () -> {
            log.info("claim-MergeSettlement job 开始");
            try {
                claimBillService.mergeClaimSettlement();
            } catch (Exception e) {
                log.info(e.getMessage(), e);
            }
            log.info("claim-MergeSettlement job 结束");
        }, -1, 1);
    }
}

