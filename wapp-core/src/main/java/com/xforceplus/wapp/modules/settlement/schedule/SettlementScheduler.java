package com.xforceplus.wapp.modules.settlement.schedule;

import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.settlement.service.SettlementService;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SettlementScheduler {

    @Autowired
    private SettlementService settlementService;
    @Autowired
    private PreinvoiceService preinvoiceService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    public static String KEY = "Settlement-split";

    /**
     * 调用拆票
     */
    @Scheduled(cron="${task.SettlementScheduler-cron}")
    public void settlementSplit(){
        if (!redisTemplate.opsForValue().setIfAbsent(KEY, KEY)) {
            log.info("Settlement-split  job 已经在执行，结束此次执行");
            return;
        }
        redisTemplate.opsForValue().set(KEY, KEY, 2, TimeUnit.HOURS);
        log.info("Settlement-split  job 开始");
        try {
            Long id = 0L;
            Integer status = TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode();
            Integer limit = 100;
            List<TXfSettlementEntity> list = settlementService.querySettlementByStatus(id, status, limit);
            while (CollectionUtils.isNotEmpty(list)) {
                for (TXfSettlementEntity tXfSettlementEntity : list) {
                    try {
                        preinvoiceService.splitPreInvoice(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getSellerNo());
                    } catch (Exception e) {
                        log.error("定时器 拆票失败：{}", e);
                    }
                }
                id =  list.stream().mapToLong(TXfSettlementEntity::getId).max().getAsLong();
                list = settlementService.querySettlementByStatus(id, status, limit);
            }
        } catch (Exception e) {
            log.info("Settlement-split job 异常：{}",e);
        }finally {
            try {
                redisTemplate.delete(KEY);
            } catch (Exception e) {
                log.info("Settlement-split  释放锁Redis 异常： {}", e);
            }
            log.info("Settlement-split  job 结束");
        }
    }
}
