package com.xforceplus.wapp.modules.deduct.schedule;

import com.xforceplus.wapp.modules.deduct.service.AgreementSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.xforceplus.wapp.client.LockClient;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AgreementDeductScheduler {

    @Autowired
    private AgreementSchedulerService agreementSchedulerService;
    public static String KEY = "Agreement-MergeSettlement";
    @Autowired
    private LockClient lockClient;

    /**
     * 协议单合并结算单
     */
    @Async("taskThreadPoolExecutor")
    @Scheduled(cron = "${task.AgreementDeductScheduler-cron}")
    public void AgreementDeductDeal() {
        log.info("Agreement-MergeSettlement job 开始");
        lockClient.tryLock(KEY, () -> {
            log.info("Agreement-MergeSettlement job 获取锁");
            try {
//                agreementBillService.mergeEPDandAgreementSettlement(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL, TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT, TXfDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT);
                //超期协议单执行合并结算单
                agreementSchedulerService.makeSettlementByScheduler();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            log.info("Agreement-MergeSettlement job 获取锁结束");
        }, -1, 1);
        log.info("Agreement-MergeSettlement job 结束");
    }
}

