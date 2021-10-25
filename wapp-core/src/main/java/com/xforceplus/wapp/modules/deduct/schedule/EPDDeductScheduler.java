package com.xforceplus.wapp.modules.deduct.schedule;

import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.service.EPDService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EPDDeductScheduler {
    @Autowired
    private EPDService epdService;
    public static String KEY = "EPD-MergeSettlement";
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * EPD 合并结算单
     */
    @Scheduled(cron=" 0 0 5 * * ?")
    public void EPDDeductDeal(){
        if (!redisTemplate.opsForValue().setIfAbsent(KEY, KEY)) {
            log.info("EPD-MergeSettlement job 已经在执行，结束此次执行");
            return;
        }
        log.info("EPD-MergeSettlement job 开始");
        epdService.mergeEPDandAgreementSettlement(XFDeductionBusinessTypeEnum.EPD_BILL, TXfBillDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT, TXfBillDeductStatusEnum.EPD_MATCH_SETTLEMENT);
        redisTemplate.delete(KEY);
        log.info("EPD-MergeSettlement job 结束");
    }
}
