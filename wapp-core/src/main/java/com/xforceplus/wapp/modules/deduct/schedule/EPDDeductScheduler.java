package com.xforceplus.wapp.modules.deduct.schedule;

import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.claim.service.ClaimService;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.modules.deduct.service.EPDService;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class EPDDeductScheduler {
    @Autowired
    private EPDService epdService;

   // @Scheduled(cron=" 0 0 0 */7 * ?") //每七天执行一次
    public void EPDDeductDeal(){
        epdService.mergeEPDandAgreementSettlement(XFDeductionBusinessTypeEnum.EPD_BILL, TXfBillDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT, TXfBillDeductStatusEnum.EPD_MATCH_SETTLEMENT);

    }
}
