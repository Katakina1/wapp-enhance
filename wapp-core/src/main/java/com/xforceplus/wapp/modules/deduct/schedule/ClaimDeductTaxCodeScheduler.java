package com.xforceplus.wapp.modules.deduct.schedule;


import com.xforceplus.wapp.modules.deduct.service.ClaimBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
@Slf4j
/**
 * 定时补充税编
 */
public class ClaimDeductTaxCodeScheduler {
//    @PostConstruct
//    public void initData() {
//
//        claimBillService.matchClaimBill();
//
//        claimBillService.claimMatchBlueInvoice();
//    }
    @Autowired
    private ClaimBillService claimBillService;

   // @Scheduled(cron=" 0 0 0 */7 * ?") //每七天执行一次
    public void AgreementDeductDeal(){

        claimBillService.matchClaimBill();

        claimBillService.claimMatchBlueInvoice();
    }

}
