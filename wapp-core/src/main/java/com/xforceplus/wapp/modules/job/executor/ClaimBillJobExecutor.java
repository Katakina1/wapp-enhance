package com.xforceplus.wapp.modules.job.executor;

import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.modules.job.chain.ClaimBillJobChain;
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

import static com.xforceplus.wapp.enums.BillJobTypeEnum.CLAIM_BILL_JOB;

/**
 * @program: wapp-enhance
 * @description: 执行索赔单任务
 * @author: Kenny Wong
 * @create: 2021-10-13 10:37
 **/
@Slf4j
@Component
public class ClaimBillJobExecutor extends AbstractBillJobExecutor {

    @Autowired
    private BillJobService billJobService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private LockClient lockClient;

    @Async("taskThreadPoolExecutor")
    @Scheduled(cron = "${claimBill.parse-cron}")
    @Override
    public void execute() {
        log.info("启动原始索赔单任务执行器");
        List<Map<String, Object>> availableJobs = billJobService.obtainAvailableJobs(CLAIM_BILL_JOB.getJobType());
        log.info("启动原始索赔单任务执行器 任务数量:{}", availableJobs.size());
        Chain chain = new ClaimBillJobChain(applicationContext);
        availableJobs.forEach(
                availableJob -> {
                    Context context = new ContextBase(availableJob);
                    Integer jobId = Integer.parseInt(String.valueOf(availableJob.get(TXfBillJobEntity.ID)));
                    boolean isLock = lockClient.tryLock("claimBillJobExecutor:" + jobId, () -> {
                        long start = System.currentTimeMillis();
                        TXfBillJobEntity billJobEntity = billJobService.getById(jobId);
                        if (Objects.equals(billJobEntity.getJobStatus(), BillJobStatusEnum.DONE.getJobStatus())) {
                            return;
                        }
                        try {
                            if (chain.execute(context)) {
                                context.put(TXfBillJobEntity.JOB_STATUS, BillJobStatusEnum.DONE.getJobStatus());
                            }
                        } catch (Exception e) {
                            log.error("execute error jobId:{}",jobId, e);
                        } finally {
                            billJobService.saveContext(context);
                        }
                        log.info("索赔单JOBID:{} 处理花费时间{}ms", context.get(TXfBillJobEntity.ID), System.currentTimeMillis() - start);
                    }, -1, 1);
                    if (!isLock) {
                        log.warn("索赔单job任务锁定失败，放弃执行 jobId:{}", jobId);
                    }
                }
        );
        log.info("原始索赔单任务执行器完成");

    }

}

