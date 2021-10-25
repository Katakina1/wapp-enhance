package com.xforceplus.wapp.modules.settlement.schedule;

import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.settlement.service.SettlementService;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 结算单重新匹配税编
 */
@Component
@Slf4j
public class SettlementTaxCodeScheduler {

    @Autowired
    private SettlementService settlementService;
    @Autowired
    private PreinvoiceService preinvoiceService;

    /**
     *  补齐税编
     */
    @Scheduled(cron=" 0 0 20 * * ?")
    public void settlementFixTaxCode(){
        log.info("settlementFixTaxCode job 开始");
        Long id = 0L;
        Integer status = TXfSettlementStatusEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode();
        Integer limit = 100;
        List<TXfSettlementEntity> list = settlementService.querySettlementByStatus(id, status, limit);
        while (CollectionUtils.isNotEmpty(list)) {
            for (TXfSettlementEntity tXfSettlementEntity : list) {
                try {
                    preinvoiceService.reFixTaxCode(tXfSettlementEntity.getSettlementNo() );
                } catch (Exception e) {
                    log.error("定时器 拆票失败：{}", e);
                }
            }
            id =  list.stream().mapToLong(TXfSettlementEntity::getId).max().getAsLong();
            list = settlementService.querySettlementByStatus(id, status, limit);
        }
        log.info("settlementFixTaxCode job 结束");
    }
}
