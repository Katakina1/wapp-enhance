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

@Component
@Slf4j
public class SettlementScheduler {

    @Autowired
    private SettlementService settlementService;
    @Autowired
    private PreinvoiceService preinvoiceService;
    /**
     * 调用拆票
     */
    @Scheduled(cron=" 0 0 6 * * ?")
    public void settlementSplit(){
        Long id = 0L;
        Integer status = TXfSettlementStatusEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode();
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
    }
}
