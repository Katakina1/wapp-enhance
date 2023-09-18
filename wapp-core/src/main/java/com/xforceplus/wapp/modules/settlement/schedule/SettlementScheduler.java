package com.xforceplus.wapp.modules.settlement.schedule;

import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.settlement.service.SettlementService;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SettlementScheduler {

    @Autowired
    private SettlementService settlementService;
    @Autowired
    private PreinvoiceService preinvoiceService;
    @Autowired
    private LockClient lockClient;
    public static String KEY = "Settlement-split";

    /**
     * .调用拆票
     */
    @Async("taskThreadPoolExecutor")
    @Scheduled(cron = "${task.SettlementScheduler-cron}")
    public void settlementSplit() {
        log.info("Settlement-split  job 开始");
        lockClient.tryLock(KEY, () -> {
            log.info("Settlement-split  job 获取锁开始");
            try {
                Long id = 0L;
                Integer status = TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode();
                Integer limit = 100;
                int count = 0;
                List<TXfSettlementEntity> tXfSettlementEntities = new ArrayList<>();
                List<TXfSettlementEntity> list = settlementService.querySettlementByStatus(id, status, limit);
                while (CollectionUtils.isNotEmpty(list)) {
                    for (TXfSettlementEntity tXfSettlementEntity : list) {
                        try {
                            if(StringUtils.contains(tXfSettlementEntity.getRemark(),"拆票失败")){
                                log.warn("结算单:{}之前拆票失败,原因:{},这次不执行拆票",tXfSettlementEntity.getSettlementNo(),tXfSettlementEntity.getRemark());
                                continue;
                            }
                            preinvoiceService.splitPreInvoice(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getSellerNo());
                        } catch (Exception e) {
                            log.error("定时器 拆票失败：{}", e);
                        }
                        count++;
                    }
                    id = list.stream().mapToLong(TXfSettlementEntity::getId).max().getAsLong();
                    list = settlementService.querySettlementByStatus(id, status, limit);
                    tXfSettlementEntities.addAll(list);
                }
                List<String> settlementNos = tXfSettlementEntities.stream().map(TXfSettlementEntity::getSettlementNo).collect(Collectors.toList());
                log.info("SettlementScheduler本次执行数量:{},结算单号:{}",count,settlementNos);
            } catch (Exception e) {
                log.info("Settlement-split job 异常：{}", e);
            }
            log.info("Settlement-split  job 获取锁结束");
        }, -1, 1);
        log.info("Settlement-split  job 结束");

    }
}
