package com.xforceplus.wapp.threadpool.callable;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.service.CommClaimService;
import com.xforceplus.wapp.service.CommSettlementService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 红字信息表撤销--结算单撤销
 * @date : 2022/09/16 15:09
 **/
@Slf4j
public class SettlementDestroyCallable implements Callable<Boolean> {

    private TXfPreInvoiceEntity preInvoiceEntity;

    private LockClient lockClient;

    private CommSettlementService commSettlementService;

    public SettlementDestroyCallable(TXfPreInvoiceEntity preInvoiceEntity, LockClient lockClient, CommSettlementService commSettlementService) {
        this.preInvoiceEntity = preInvoiceEntity;
        this.lockClient = lockClient;
        this.commSettlementService = commSettlementService;
    }

    @Override
    public Boolean call() throws Exception {
        log.info("SettlementDestroyCallable begin:[{}]", JSON.toJSONString(preInvoiceEntity));
        String key = "SettlementDestroyCallable:" + preInvoiceEntity.getSettlementId();
        try {
            lockClient.tryLock(key, () -> commSettlementService.destroySettlement(preInvoiceEntity), -1, 1);
        } catch (Exception e) {
            log.error("SettlementDestroyCallable error:{}-", e.getMessage(), e);
        }
        return true;
    }

}
