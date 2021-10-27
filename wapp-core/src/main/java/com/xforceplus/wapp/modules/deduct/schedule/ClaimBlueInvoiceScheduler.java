package com.xforceplus.wapp.modules.deduct.schedule;

import com.xforceplus.wapp.modules.deduct.service.ClaimBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ClaimBlueInvoiceScheduler {

    @Autowired
    private ClaimBillService claimBillService;
    public static String KEY = "Claim-match-blueInfo";
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 索赔单匹配
     */
    @Scheduled(cron=" 0 0 1 * * ?")
    public void claimBlueInfoDeal(){
        if (!redisTemplate.opsForValue().setIfAbsent(KEY, KEY)) {
            log.info("Claim-match-blueInfo  job 已经在执行，结束此次执行");
            return;
        }
        redisTemplate.opsForValue().set(KEY, KEY, 2, TimeUnit.HOURS);
        log.info("Claim-match-blueInfo job 已经在执行，开始");
        try {
            claimBillService.claimMatchBlueInvoice();
        } catch (Exception e) {
            log.info("Claim-match-blueInfo job 异常：{}",e);
        }finally {
            try {
                redisTemplate.delete(KEY);
            } catch (Exception e) {
                log.info("Claim-match-blueInfo  释放锁Redis 异常： {}", e);
            }
            log.info("Claim-match-blueInfo  job 已经在执行，结束");
        }
    }

}
