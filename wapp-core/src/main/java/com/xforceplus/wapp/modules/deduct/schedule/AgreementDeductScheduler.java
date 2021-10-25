package com.xforceplus.wapp.modules.deduct.schedule;

import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.service.AgreementBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
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
            return;
        }
        agreementBillService.mergeEPDandAgreementSettlement(XFDeductionBusinessTypeEnum.AGREEMENT_BILL, TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT, TXfBillDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT);
        redisTemplate.delete(KEY);
    }
}
