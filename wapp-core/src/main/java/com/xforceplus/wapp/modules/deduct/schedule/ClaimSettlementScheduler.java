package com.xforceplus.wapp.modules.deduct.schedule;

import com.xforceplus.wapp.modules.deduct.service.ClaimBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

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
            log.info("claim-MergeSettlement job 已经在执行，结束此次执行");
            return;
        }
        redisTemplate.opsForValue().set(KEY, KEY, 2, TimeUnit.HOURS);
        log.info("claim-MergeSettlement job 开始");
        try {
            claimBillService.mergeClaimSettlement();
        } catch (Exception e) {
            log.info("claim-MergeSettlement job 异常：{}",e);
        }finally {
            try {
                redisTemplate.delete(KEY);
            } catch (Exception e) {
                log.info("claim-MergeSettlement  释放锁Redis 异常： {}", e);
            }
            log.info("claim-MergeSettlement job 结束");
        }
    }

}
