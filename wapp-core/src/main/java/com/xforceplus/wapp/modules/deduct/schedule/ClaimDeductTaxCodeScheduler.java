package com.xforceplus.wapp.modules.deduct.schedule;


import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.enums.TXfDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.service.ClaimBillService;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
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

    public static String KEY = "matchTaxCode";

    @Autowired
    private LockClient lockClient;


    /**
     * 索赔单匹配税编
     */
    @Async("taskThreadPoolExecutor")
    @Scheduled(cron = "${task.ClaimDeductTaxCodeScheduler-cron}")
    public void matchTaxCode() {
        log.info("claim-matchTaxCode job job 开始");
        lockClient.tryLock(KEY, () -> {
            log.info("claim-matchTaxCode job job 获取锁成功");
            try {
                Long id = 0L;
                List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(id, null, 10, TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfDeductStatusEnum.CLAIM_NO_MATCH_TAX_NO.getCode());
                while (CollectionUtils.isNotEmpty(tXfBillDeductEntities)) {
                    for (TXfBillDeductEntity tmp : tXfBillDeductEntities) {
                        claimBillService.reMatchClaimTaxCode(tmp.getId(),tmp.getBusinessNo());
                    }
                    id = tXfBillDeductEntities.stream().mapToLong(TXfBillDeductEntity::getId).max().getAsLong();
                    tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(id, null, 10, TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfDeductStatusEnum.CLAIM_NO_MATCH_TAX_NO.getCode());
                }
            } catch (Exception e) {
                log.info(e.getMessage(), e);
            }
            log.info("claim-matchTaxCode job job 结束");
        }, -1, 1);

    }
}
