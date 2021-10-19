package com.xforceplus.wapp.modules.job.executor;

import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.modules.job.chain.EpdBillJobChain;
import com.xforceplus.wapp.modules.job.service.BillJobService;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.springframework.beans.factory.annotation.Autowired;
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

    // TODO 添加异步处理
    @Scheduled(cron = "* * 0 * * ?")
    @Override
    public void execute() {
        List<Map<String, Object>> availableJobs = billJobService.obtainAvailableJobs(EPD_BILL_JOB.getJobType());
        Chain chain = new EpdBillJobChain();
        availableJobs.forEach(
                availableJob -> {
                    Integer jobId = Integer.parseInt(String.valueOf(availableJob.get(TXfBillJobEntity.ID)));
                    Context context = new ContextBase(availableJob);
                    try {
                        if (billJobService.lockJob(jobId)) {
                            if (chain.execute(context)) {
                                executePostAction(context);
                            }
                        } else {
                            log.warn("EPD job任务锁定失败，放弃执行，jobId={}", jobId);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    } finally {
                        billJobService.unlockJob(jobId);
                    }
                }
        );
    }

    private void executePostAction(Context context) {
        context.put(TXfBillJobEntity.JOB_STATUS, BillJobStatusEnum.DONE.getJobStatus());
        saveContext(context);
        // 触发下游任务
        deductService.receiveDone(XFDeductionBusinessTypeEnum.EPD_BILL);
    }

    /**
     * 保存context瞬时状态入库
     *
     * @param context
     * @return
     */
    private boolean saveContext(Context context) {
        TXfBillJobEntity tXfBillJobEntity = BeanUtils.mapToBean(context, TXfBillJobEntity.class);
        return billJobService.updateById(tXfBillJobEntity);
    }
}
