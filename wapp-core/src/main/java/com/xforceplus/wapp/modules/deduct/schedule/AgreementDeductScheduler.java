package com.xforceplus.wapp.modules.deduct.schedule;

import com.xforceplus.wapp.enums.TXfDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.service.AgreementBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AgreementDeductScheduler {

    @Autowired
    private AgreementBillService agreementBillService;
    public static String KEY = "Agreement-MergeSettlement";
    @Autowired
    private StringRedisTemplate redisTemplate;
     /**
     * 协议单合并结算单
     */
    @Scheduled(cron="${task.AgreementDeductScheduler-cron}")
    public void AgreementDeductDeal(){
         if (!redisTemplate.opsForValue().setIfAbsent(KEY, KEY)) {
                log.info("Agreement-MergeSettlement job 已经在执行，结束此次执行");
                return;
         }
        redisTemplate.opsForValue().set(KEY, KEY, 2, TimeUnit.HOURS);
        log.info("Agreement-MergeSettlement job 开始");
        try {
            agreementBillService.mergeEPDandAgreementSettlement(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL, TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT, TXfDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT);
        } catch (Exception e) {
            log.info("Agreement-MergeSettlement job 异常：{}",e);
        }finally {
            try {
                redisTemplate.delete(KEY);
            } catch (Exception e) {
                log.info("Agreement-MergeSettlement job 释放锁Redis： {}", e);
            }
            log.info("Agreement-MergeSettlement job 结束");
        }
    }
}
