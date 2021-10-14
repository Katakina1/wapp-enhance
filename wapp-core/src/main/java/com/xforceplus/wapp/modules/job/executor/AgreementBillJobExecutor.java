package com.xforceplus.wapp.modules.job.executor;

import com.xforceplus.wapp.modules.job.chain.AgreementBillJobChain;
import com.xforceplus.wapp.modules.job.service.BillJobService;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
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

    // TODO 添加定时任务
    // TODO 添加异步处理
    @Override
    public void execute() {
        List<Map<String, Object>> availableJobs = billJobService.obtainAvailableJobs();
        Chain chain = new AgreementBillJobChain();
        availableJobs.forEach(
                availableJob -> {
                    Integer id = Integer.parseInt(String.valueOf(availableJob.get(TXfBillJobEntity.ID)));
                    Context context = new ContextBase(availableJob);
                    try {
                        billJobService.lockJob(id);
                        chain.execute(context);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    } finally {
                        billJobService.unlockJob(id);
                    }
                }
        );
    }
}
