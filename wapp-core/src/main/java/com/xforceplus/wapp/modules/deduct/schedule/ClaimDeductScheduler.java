package com.xforceplus.wapp.modules.deduct.schedule;

import com.xforceplus.wapp.modules.deduct.service.ClaimBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Component
@Slf4j
public class ClaimDeductScheduler {

    @Autowired
    private ClaimBillService claimBillService;

    /**
     * 索赔单匹配
     */
    @Scheduled(cron=" 0 0 1 * * ?")
    public void claimDeductDeal(){
        claimBillService.matchClaimBill();
        claimBillService.claimMatchBlueInvoice();
    }

}
