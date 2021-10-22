package com.xforceplus.wapp.modules.deduct.schedule;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.model.ClaimBillData;
import com.xforceplus.wapp.modules.deduct.model.ClaimBillItemData;
import com.xforceplus.wapp.modules.deduct.model.DeductBillBaseData;
import com.xforceplus.wapp.modules.deduct.service.AgreementBillService;
import com.xforceplus.wapp.modules.job.executor.AgreementBillJobExecutor;
import com.xforceplus.wapp.modules.job.executor.ClaimBillJobExecutor;
import com.xforceplus.wapp.modules.job.executor.EpdBillJobExecutor;
import com.xforceplus.wapp.modules.job.generator.ClaimBillJobGenerator;
import com.xforceplus.wapp.modules.job.generator.EpdBillJobGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class AgreementDeductScheduler {
    @Autowired
    private AgreementBillJobExecutor agreementBillJobExecutor;
    @Autowired
    private ClaimBillJobExecutor claimBillJobExecutor;
    @Autowired
    private EpdBillJobExecutor epdBillJobExecutor;

    @PostConstruct
    private void init() {
       // agreementBillJobExecutor.execute();
      claimBillJobExecutor.execute();
      epdBillJobExecutor.execute();
    }
    @Autowired
    private AgreementBillService agreementBillService;
   // @Scheduled(cron=" 0 0 0 */7 * ?") //每七天执行一次
    public void AgreementDeductDeal(){
        agreementBillService.mergeEPDandAgreementSettlement(XFDeductionBusinessTypeEnum.AGREEMENT_BILL, TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT, TXfBillDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT);
     }
}
