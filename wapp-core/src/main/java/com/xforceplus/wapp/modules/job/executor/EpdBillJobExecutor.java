package com.xforceplus.wapp.modules.job.executor;

import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.modules.job.chain.EpdBillJobChain;
import com.xforceplus.wapp.modules.job.service.BillJobService;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang.math.NumberUtils;
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
    private DeductService deductService;
    @Autowired
    private ApplicationContext applicationContext;

    @Async
    @Scheduled(cron = "* * 0 * * ?")
    @Override
    public void execute() {
        List<Map<String, Object>> availableJobs = billJobService.obtainAvailableJobs(EPD_BILL_JOB.getJobType());
        Chain chain = new EpdBillJobChain(applicationContext);
        availableJobs.forEach(
                availableJob -> {
                    Integer jobId = Integer.parseInt(String.valueOf(availableJob.get(TXfBillJobEntity.ID)));
                    Context context = new ContextBase(availableJob);
                    try {
                        if (billJobService.lockJob(jobId)) {
                            if (chain.execute(context)) {
                                context.put(TXfBillJobEntity.JOB_STATUS, BillJobStatusEnum.DONE.getJobStatus());
                            }
                        } else {
                            log.warn("EPD job任务锁定失败，放弃执行，jobId={}", jobId);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    } finally {
                        saveContext(context);
                        billJobService.unlockJob(jobId);
                    }
                }
        );
    }

    /**
     * 保存context瞬时状态入库
     *
     * @param context
     * @return
     */
    private boolean saveContext(Context context) {
        TXfBillJobEntity tXfBillJobEntity = new TXfBillJobEntity();
        tXfBillJobEntity.setId(Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.ID))));
        tXfBillJobEntity.setJobStatus(Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_STATUS))));
        tXfBillJobEntity.setRemark(String.valueOf(context.get(TXfBillJobEntity.REMARK)));
        if (NumberUtils.isNumber(String.valueOf(context.get(TXfBillJobEntity.JOB_ACQUISITION_OBJECT)))) {
            tXfBillJobEntity.setJobAcquisitionObject(Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_ACQUISITION_OBJECT))));
        }
        if (NumberUtils.isNumber(String.valueOf(context.get(TXfBillJobEntity.JOB_ACQUISITION_PROGRESS)))) {
            tXfBillJobEntity.setJobAcquisitionProgress(Long.parseLong(String.valueOf(context.get(TXfBillJobEntity.JOB_ACQUISITION_PROGRESS))));
        }
        if (NumberUtils.isNumber(String.valueOf(context.get(TXfBillJobEntity.JOB_ENTRY_OBJECT)))) {
            tXfBillJobEntity.setJobEntryObject(Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_ENTRY_OBJECT))));
        }
        if (NumberUtils.isNumber(String.valueOf(context.get(TXfBillJobEntity.JOB_ENTRY_PROGRESS)))) {
            tXfBillJobEntity.setJobEntryProgress(Long.parseLong(String.valueOf(context.get(TXfBillJobEntity.JOB_ENTRY_PROGRESS))));
        }
        return billJobService.updateById(tXfBillJobEntity);
    }
}
