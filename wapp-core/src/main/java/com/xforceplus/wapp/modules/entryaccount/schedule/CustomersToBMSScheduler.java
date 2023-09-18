package com.xforceplus.wapp.modules.entryaccount.schedule;

import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.modules.customs.service.CustomsService;
import com.xforceplus.wapp.modules.entryaccount.service.EntryAccountService;
import com.xforceplus.wapp.repository.entity.TDxCustomsEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 根据海关缴款书号码定时去BMS获取海关缴款书明细信息
 *
 * @Author: ChenHang
 * @Date: 2023/7/3 16:35
 */
@Component
@Slf4j
public class CustomersToBMSScheduler {

    @Autowired
    private EntryAccountService entryAccountService;

    @Autowired
    private CustomsService customsService;

    public static String KEY = "Customer-BMS";
    @Autowired
    private LockClient lockClient;

    /**
     * 获取BMS海关缴款书明细及推送比对状态
     */
    @Async("taskThreadPoolExecutor")
//    @Scheduled(cron = "${task.customerBMS-cron}")
    public void customerToBMS() {
        log.info("Customer-BMS job 开始");
        lockClient.tryLock(KEY, () -> {
            log.info("Customer-BMS job 获取锁");
            List<TDxCustomsEntity> list = customsService.getTaskCustoms();
            for (TDxCustomsEntity tDxCustomsEntity : list) {
                try {
                    entryAccountService.customerToBMS(tDxCustomsEntity);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            log.info("Customer-BMS job 获取锁结束");
        }, -1, 1);
        log.info("Customer-BMS job 结束");
    }

}
