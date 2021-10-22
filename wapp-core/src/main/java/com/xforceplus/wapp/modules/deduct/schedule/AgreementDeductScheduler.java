package com.xforceplus.wapp.modules.deduct.schedule;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.service.AgreementBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AgreementDeductScheduler {
    @Autowired
    private AgreementBillService agreementBillService;
    @Scheduled(cron=" 0 0 0 */7 * ?") //每七天执行一次
    public void AgreementDeductDeal(){
        agreementBillService.mergeEPDandAgreementSettlement(XFDeductionBusinessTypeEnum.AGREEMENT_BILL, TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT, TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT, null);
     }
}