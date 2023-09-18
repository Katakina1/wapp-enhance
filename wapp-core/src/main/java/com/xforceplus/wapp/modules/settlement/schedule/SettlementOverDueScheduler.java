package com.xforceplus.wapp.modules.settlement.schedule;

import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.settlement.service.SettlementService;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
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
    private LockClient lockClient;
    public static String KEY = "Settlement-overdraft";
    private static Integer STEP = 3;

    /**
     * 自动确认逾期金额 结算单下 所属的单据 全部预期，自动确认
     */
    @Async("taskThreadPoolExecutor")
    @Scheduled(cron = "${task.SettlementOverDueScheduler-cron}")
    public void settlementAutoConfirm() {
        log.info("Settlement-overdraft  job 开始");
        lockClient.tryLock(KEY, () -> {
            log.info("Settlement-overdraft  job 获取到锁开始");
            try {
                Long id = 0L;
                Integer status = TXfSettlementStatusEnum.WAIT_CONFIRM.getCode();
                Integer limit = 100;
//                Date reference = DateUtils.addDate(new Date(),  - STEP);
                Date reference = new Date();
                List<TXfSettlementEntity> list = settlementService.querySettlementByStatus(id, status, limit);
                while (CollectionUtils.isNotEmpty(list)) {
                    for (TXfSettlementEntity tXfSettlementEntity : list) {
                        if (tXfSettlementEntity.getCreateTime().after(reference)) {
                            continue;
                        }
                        try {
                            preinvoiceService.reCalculation(tXfSettlementEntity.getSettlementNo());
                        } catch (Exception e) {
                            log.error("定时器 拆票失败:"+e.getMessage(), e);
                        }
                    }
                    id = list.stream().mapToLong(TXfSettlementEntity::getId).max().getAsLong();
                    list = settlementService.querySettlementByStatus(id, status, limit);
                }
            } catch (Exception e) {
                log.info(e.getMessage(), e);
            }
            log.info("Settlement-overdraft  job 获取锁结束");
        }, -1, 1);
        log.info("Settlement-overdraft  job 结束");


    }
}

