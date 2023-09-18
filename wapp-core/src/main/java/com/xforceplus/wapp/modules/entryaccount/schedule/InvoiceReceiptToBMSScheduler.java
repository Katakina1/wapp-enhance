package com.xforceplus.wapp.modules.entryaccount.schedule;

import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.modules.entryaccount.service.BMSService;
import com.xforceplus.wapp.repository.entity.TDxCustomsEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 对接BMS非商签收数据推送BMS
 * @Author: ChenHang
 * @Date: 2023/7/6 17:04
 */
@Component
@Slf4j
public class InvoiceReceiptToBMSScheduler {

    @Autowired
    private BMSService bmsService;
    public static String KEY = "InvoiceReceipt-BMS";

    @Autowired
    private LockClient lockClient;

    /**
     * 推送已签收发票数据进BMS
     */
    @Async("taskThreadPoolExecutor")
    @Scheduled(cron = "${task.InvoiceReceiptBMS-cron}")
    public void invoiceReceiptToBMS() {
        log.info("InvoiceReceipt-BMS job 开始");
        lockClient.tryLock(KEY, () -> {
            log.info("InvoiceReceipt-BMS job 获取锁");
            try {
                bmsService.qsToBms();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            log.info("InvoiceReceipt-BMS job 获取锁结束");
        }, -1, 1);
        log.info("InvoiceReceipt-BMS job 结束");
    }

}
