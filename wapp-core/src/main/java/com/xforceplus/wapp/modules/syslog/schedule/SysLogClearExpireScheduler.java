package com.xforceplus.wapp.modules.syslog.schedule;

import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.modules.deduct.service.AgreementBillService;
import com.xforceplus.wapp.modules.syslog.service.SysLogBatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SysLogClearExpireScheduler {

    @Autowired
    private SysLogBatchService sysLogBatchService;
    public static String KEY = "SysLog-ClearExpire";
    @Autowired
    private LockClient lockClient;

    /**
     * 日志
     */
    @Async("taskThreadPoolExecutor")
    @Scheduled(cron = "${task.SysLogClearExpireScheduler-cron}")
    public void sysLogClearExpire() {
        log.info("SysLog-ClearExpire job 开始");
        lockClient.tryLock(KEY, () -> {
            log.info("SysLog-ClearExpire job 获取锁");
            try {
                sysLogBatchService.clearExpire();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            log.info("SysLog-ClearExpire job 获取锁结束");
        }, -1, 1);
        log.info("SysLog-ClearExpire job 结束");
    }
}

