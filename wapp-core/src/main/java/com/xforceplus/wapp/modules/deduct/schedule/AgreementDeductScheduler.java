package com.xforceplus.wapp.modules.deduct.schedule;

import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AgreementDeductScheduler {
    @Autowired
    private DeductService deductService;
    @Scheduled(cron=" 0 0 0 */7 * ?") //每七天执行一次
    public void AgreementDeductDeal(){
        deductService.receiveDone(XFDeductionBusinessTypeEnum.AGREEMENT_BILL);
    }
}
