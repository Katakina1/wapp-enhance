package com.xforceplus.wapp.modules.deduct.schedule;

import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.modules.deduct.service.ClaimBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ClaimBlueInvoiceScheduler {

    @Autowired
    private ClaimBillService claimBillService;
    public static String KEY = "Claim-match-blueInfo";
    @Autowired
    private LockClient lockClient;

    /**
     * 索赔单匹配
     */
    @Async("taskThreadPoolExecutor")
    @Scheduled(cron = "${task.ClaimBlueInvoiceScheduler-cron}")
    public void claimBlueInfoDeal() {
        lockClient.tryLock(KEY, () -> {
            log.info("Claim-match-blueInfo job 已经在执行，开始");
            try {
                claimBillService.claimMatchBlueInvoice();
            } catch (Exception e) {
                log.info(e.getMessage(), e);
            }
            log.info("Claim-match-blueInfo  job 已经在执行，结束");
        }, -1, 1);

    }

}

