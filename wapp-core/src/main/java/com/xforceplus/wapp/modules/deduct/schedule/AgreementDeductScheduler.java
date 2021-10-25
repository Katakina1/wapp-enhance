package com.xforceplus.wapp.modules.deduct.schedule;

import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.service.AgreementBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AgreementDeductScheduler {

    @Autowired
    private AgreementBillService agreementBillService;
    public static String KEY = "Agreement-MergeSettlement";
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 协议单合并结算单
     */
    @Scheduled(cron=" 0 0 4 * * ?")
    public void AgreementDeductDeal(){
        if (!redisTemplate.opsForValue().setIfAbsent(KEY, KEY)) {
            log.info("Agreement-MergeSettlement job 已经在执行，结束此次执行");
            return;
        }
        log.info("Agreement-MergeSettlement job 开始");
        agreementBillService.mergeEPDandAgreementSettlement(XFDeductionBusinessTypeEnum.AGREEMENT_BILL, TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT, TXfBillDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT);
        redisTemplate.delete(KEY);
        log.info("Agreement-MergeSettlement job 结束");
    }
}
