package com.xforceplus.wapp.modules.job.executor;

import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.modules.job.chain.EpdBillJobChain;
import com.xforceplus.wapp.modules.job.service.BillJobService;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.enums.BillJobTypeEnum.EPD_BILL_JOB;

/**
 * @program: wapp-enhance
 * @description: 执行EPD单任务
 * @author: Kenny Wong
 * @create: 2021-10-13 10:37
 **/
@Slf4j
@Component
public class EpdBillJobExecutor extends AbstractBillJobExecutor {

    @Autowired
    private BillJobService billJobService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private LockClient lockClient;

    @Async
    @Scheduled(cron = "${epdBill.parse-cron}")
    @Override
    public void execute() {
        log.info("启动原始EPD单任务执行器");
        List<Map<String, Object>> availableJobs = billJobService.obtainAvailableJobs(EPD_BILL_JOB.getJobType());
        Chain chain = new EpdBillJobChain(applicationContext);
        availableJobs.forEach(
                availableJob -> {
                    Integer jobId = Integer.parseInt(String.valueOf(availableJob.get(TXfBillJobEntity.ID)));
                    Context context = new ContextBase(availableJob);
                    boolean isLock = lockClient.tryLock("epdBillJobExecutor:" + jobId, () -> {
                        try {
                            if (chain.execute(context)) {
                                context.put(TXfBillJobEntity.JOB_STATUS, BillJobStatusEnum.DONE.getJobStatus());
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        } finally {
                            billJobService.saveContext(context);
                        }
                    }, -1, 1);
                    if (!isLock) {
                        log.warn("EPD job任务锁定失败，放弃执行，jobId={}", jobId);
                    }

                }
        );
    }

}
