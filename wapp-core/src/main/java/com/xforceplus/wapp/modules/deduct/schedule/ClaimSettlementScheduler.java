package com.xforceplus.wapp.modules.deduct.schedule;

import com.xforceplus.wapp.modules.deduct.service.ClaimBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ClaimSettlementScheduler {

    @Autowired
    private ClaimBillService claimBillService;
    @Autowired
    private  RedisTemplate redisTemplate;
    public static String KEY = "claim-MergeSettlement";

    /**
     * 索赔单生成结算单
     */
    @Scheduled(cron=" 0 0 3 * * ?")
    public void claimMergeSettlementDeductDeal(){
        if (!redisTemplate.opsForValue().setIfAbsent(KEY, KEY)) {
            return;
        }
        claimBillService.mergeClaimSettlement();
        redisTemplate.delete(KEY);
    }

}
