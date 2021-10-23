package com.xforceplus.wapp.modules.settlement.schedule;

import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.settlement.service.SettlementService;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SettlementScheduler {

    @Autowired
    private SettlementService settlementService;
    @Autowired
    private PreinvoiceService preinvoiceService;
   // @PostConstruct
    public void initData() {
        settlementSplit();
    }
    /**
     * 调用拆票
     */
  //  @Scheduled(cron=" 0 0 0 */7 * ?") //每七天执行一次
    public void settlementSplit(){
        Long id = 0L;
        Integer status = TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode();
        Integer limit = 100;
        List<TXfSettlementEntity> list = settlementService.queryWaitSplitSettlement(id, status, limit);
        for (TXfSettlementEntity tXfSettlementEntity : list) {
            try {
                preinvoiceService.splitPreInvoice(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getSellerNo());
            } catch (Exception e) {
                log.error("定时器 拆票失败：{}", e);
            }
        }
    }

}
