package com.xforceplus.wapp.modules.settlement.schedule;

import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.settlement.service.SettlementService;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
     * 自动确认
     */
  //  @S
    //  cheduled(cron=" 0 0 0 */7 * ?") //每七天执行一次

    public void settlementAutoConfirm(){
        Long id = 0L;
        Integer status = TXfSettlementStatusEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode();
        Integer limit = 100;
        List<TXfSettlementEntity> list = settlementService.queryWaitSplitSettlement(id, status, limit);
        for (TXfSettlementEntity tXfSettlementEntity : list) {
            try {
                preinvoiceService.reFixTaxCode( tXfSettlementEntity.getSettlementNo() );
            } catch (Exception e) {
                log.error("定时器 拆票失败：{}", e);
            }
        }
    }
}
