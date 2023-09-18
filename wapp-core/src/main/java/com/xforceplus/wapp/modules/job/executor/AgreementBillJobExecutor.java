package com.xforceplus.wapp.modules.job.executor;

import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.modules.job.chain.AgreementBillJobChain;
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
import java.util.Objects;

import static com.xforceplus.wapp.enums.BillJobTypeEnum.AGREEMENT_BILL_JOB;

/**
 * sa
 *
 * @program: wapp-enhance
 * @description: 执行协议单任务
 * @author: Kenny Wong
 * @create: 2021-10-13 10:36
 **/
@Slf4j
@Component
public class AgreementBillJobExecutor extends AbstractBillJobExecutor {

    @Autowired
    private BillJobService billJobService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private LockClient lockClient;

    @Async("taskThreadPoolExecutor")
    @Scheduled(cron = "${agreementBill.parse-cron}")
    @Override
    public void execute() {
        log.info("启动原始协议单任务执行器");
        List<Map<String, Object>> availableJobs = billJobService.obtainAvailableJobs(AGREEMENT_BILL_JOB.getJobType());
        log.info("启动原始协议单任务执行器 任务数量:{}", availableJobs.size());
        Chain chain = new AgreementBillJobChain(applicationContext);
        availableJobs.forEach(
                availableJob -> {
                    Context context = new ContextBase(availableJob);
                    Integer jobId = Integer.parseInt(String.valueOf(availableJob.get(TXfBillJobEntity.ID)));
                    boolean isLock = lockClient.tryLock("agreementBillJobExecutor:" + jobId, () -> {
                        TXfBillJobEntity billJobEntity = billJobService.getById(jobId);
                        if (Objects.equals(billJobEntity.getJobStatus(), BillJobStatusEnum.DONE.getJobStatus())) {
                            return;
                        }
                        long start = System.currentTimeMillis();
                        try {
                            if (chain.execute(context)) {
                                context.put(TXfBillJobEntity.JOB_STATUS, BillJobStatusEnum.DONE.getJobStatus());
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        } finally {
                            billJobService.saveContext(context);
                        }
                        log.info("协议单:{} 处理花费时间{}ms", context.get(TXfBillJobEntity.ID), System.currentTimeMillis() - start);
                    }, -1, 1);
                    if (!isLock) {
                        log.warn("协议单job任务锁定失败，放弃执行 jobId:{}", jobId);
                    }
                }
        );
        log.info("原始协议单任务执行器完成");

    }
}

