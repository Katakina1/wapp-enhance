package com.xforceplus.wapp.modules.deduct.schedule;


import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.service.ClaimBillService;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;


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
    private StringRedisTemplate redisTemplate;
    /**
     * 索赔单匹配税编
     */
    @Scheduled(cron=" 0 0 12 * * ?")
    public void matchTaxCode(){
        if (!redisTemplate.opsForValue().setIfAbsent(KEY, KEY)) {
            log.info("claim-matchTaxCode job 已经在执行，结束此次执行");
            return;
        }
        redisTemplate.opsForValue().set(KEY, KEY, 2, TimeUnit.HOURS);
        log.info("claim-matchTaxCode job   开始");
        try {
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
        catch (Exception e) {
            log.info("claim-matchTaxCode job job 异常：{}",e);
        }finally {
            try {
                redisTemplate.delete(KEY);
            } catch (Exception e) {
                log.info("claim-matchTaxCode job job 释放锁Redis： {}", e);
            }
            log.info("claim-matchTaxCode job job 结束");
        }
    }
}
