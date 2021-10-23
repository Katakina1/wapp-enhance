package com.xforceplus.wapp.modules.deduct.schedule;


import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.service.ClaimBillService;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
/**
 * 定时补充税编
 */
public class ClaimDeductTaxCodeScheduler {

    @Autowired
    private ClaimBillService claimBillService;
    @Autowired
    protected TXfBillDeductExtDao tXfBillDeductExtDao;
   // @Scheduled(cron=" 0 0 0 */7 * ?") //每七天执行一次
    public void matchTaxCode(){
         Long id = 0L;
         List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(id,null, 10, XFDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_TAX_NO.getCode());
         while (CollectionUtils.isNotEmpty(tXfBillDeductEntities)) {
            for (TXfBillDeductEntity tmp : tXfBillDeductEntities) {
                claimBillService.reMatchClaimTaxCode(tmp.getId());
            }
            id =  tXfBillDeductEntities.stream().mapToLong(TXfBillDeductEntity::getId).max().getAsLong();
            tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(id,null, 10, XFDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_TAX_NO.getCode());
        }
    }
}
