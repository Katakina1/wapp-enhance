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
public class SettlementOverDueScheduler {

    @Autowired
    private SettlementService settlementService;
    @Autowired
    private PreinvoiceService preinvoiceService;

    /**
     * 自动确认逾期金额 结算单下 所属的单据 全部预期，自动确认
     */

    @Scheduled(cron=" 0 0 1 * * ?")
    public void settlementAutoConfirm(){
        Long id = 0L;
        Integer status = TXfSettlementStatusEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode();
        Integer limit = 100;
        List<TXfSettlementEntity> list = settlementService.querySettlementByStatus(id, status, limit);
        while (CollectionUtils.isNotEmpty(list)) {
            for (TXfSettlementEntity tXfSettlementEntity : list) {
                try {
                    preinvoiceService.reCalculation( tXfSettlementEntity.getSettlementNo() );
                } catch (Exception e) {
                    log.error("定时器 拆票失败：{}", e);
                }
            }
            id =  list.stream().mapToLong(TXfSettlementEntity::getId).max().getAsLong();
            list = settlementService.querySettlementByStatus(id, status, limit);
        }

    }
}
