package com.xforceplus.wapp.modules.settlement.schedule;

import com.xforceplus.wapp.common.utils.DateUtils;
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

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 结算单重新匹配税编
 */
@Component
@Slf4j
public class SettlementOverDueScheduler {

    @Autowired
    private SettlementService settlementService;
    @Autowired
    private PreinvoiceService preinvoiceService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    public static String KEY = "Settlement-overdraft";
    private static Integer STEP = 5;
    /**
     * 自动确认逾期金额 结算单下 所属的单据 全部预期，自动确认
     */
    @Scheduled(cron=" 0 0 1 * * ?")
    public void settlementAutoConfirm(){
        if (!redisTemplate.opsForValue().setIfAbsent(KEY, KEY)) {
            log.info("Settlement-overdraft  job 已经在执行，结束此次执行");
            return;
        }
        redisTemplate.opsForValue().set(KEY, KEY, 10, TimeUnit.MINUTES);
        log.info("Settlement-overdraft  job 开始");
        try {
            Long id = 0L;
            Integer status = TXfSettlementStatusEnum.WAIT_CONFIRM.getCode();
            Integer limit = 100;
            List<TXfSettlementEntity> list = settlementService.querySettlementByStatus(id, status, limit);
            Date date = new Date();
            Date reference = DateUtils.addDate(date, 0 - STEP);
            while (CollectionUtils.isNotEmpty(list)) {
                for (TXfSettlementEntity tXfSettlementEntity : list) {
                    if (tXfSettlementEntity.getCreateTime().before(reference)) {
                        continue;
                    }
                    try {
                        preinvoiceService.reCalculation( tXfSettlementEntity.getSettlementNo() );
                    } catch (Exception e) {
                        log.error("定时器 拆票失败：{}", e);
                    }
                }
                id =  list.stream().mapToLong(TXfSettlementEntity::getId).max().getAsLong();
                list = settlementService.querySettlementByStatus(id, status, limit);
            }
        } catch (Exception e) {
            log.info("Settlement-overdraft job 异常：{}",e);
        }finally {
            try {
                redisTemplate.delete(KEY);
            } catch (Exception e) {
                log.info("Settlement-overdraft  释放锁Redis 异常： {}", e);
            }
            log.info("Settlement-overdraft  job 结束");
        }
    }
}
