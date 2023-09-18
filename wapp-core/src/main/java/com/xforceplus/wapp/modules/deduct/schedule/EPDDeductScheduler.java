package com.xforceplus.wapp.modules.deduct.schedule;

import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.enums.TXfDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.service.EPDService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class EPDDeductScheduler {
    @Autowired
    private EPDService epdService;
    public static String KEY = "EPD-MergeSettlement";
    @Autowired
    private LockClient lockClient;

    /**
     * EPD 合并结算单
     */
    @Async("taskThreadPoolExecutor")
    @Scheduled(cron = "${task.EPDDeductScheduler-cron}")
    public void EPDDeductDeal() {
        lockClient.tryLock(KEY, () -> {
            log.info("EPD-MergeSettlement job 开始");
            try {
//                epdService.mergeEPDandAgreementSettlement(TXfDeductionBusinessTypeEnum.EPD_BILL, TXfDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT, TXfDeductStatusEnum.EPD_MATCH_SETTLEMENT);
                log.info("EPD-MergeSettlement job 结束");
            } catch (Exception e) {
                log.info(e.getMessage(), e);
            }
        }, -1, 1);

    }
}

