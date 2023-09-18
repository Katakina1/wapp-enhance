package com.xforceplus.wapp.threadpool.callable;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.service.CommClaimService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * @author : hujintao
 * @version : 1.0
 * @description :
 * @date : 2022/09/09 17:56
 **/
@Slf4j
public class ClaimSplitAgainCallable implements Callable<Boolean> {

    private TXfPreInvoiceEntity preInvoiceEntity;

    private LockClient lockClient;

    private CommClaimService commClaimService;

    public ClaimSplitAgainCallable(TXfPreInvoiceEntity preInvoiceEntity, LockClient lockClient, CommClaimService commClaimService) {
        this.preInvoiceEntity = preInvoiceEntity;
        this.lockClient = lockClient;
        this.commClaimService = commClaimService;
    }

    @Override
    public Boolean call() throws Exception {
        String key = "ClaimSplitAgainCallable:" + preInvoiceEntity.getSettlementId();
        log.info("ClaimSplitAgainCallable begin:[{}]", JSON.toJSONString(preInvoiceEntity));
        lockClient.tryLock(key, () -> commClaimService.splitAgain(preInvoiceEntity), -1, 1);
        return true;
    }
}
