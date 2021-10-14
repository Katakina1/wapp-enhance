package com.xforceplus.wapp.modules.job.executor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.modules.job.chain.EPDBillJobChain;
import com.xforceplus.wapp.repository.dao.TXfBillJobDao;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.enums.BillJobLockStatusEnum.UNLOCKED;
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
    private TXfBillJobDao tXfBillJobDao;

    // TODO 添加定时任务
    // TODO 添加异步处理
    @Override
    public void execute() {
        List<Map<String, Object>> availableJobs = getAvailableJobs();
        Chain chain = new EPDBillJobChain();
        availableJobs.forEach(
                availableJob -> {
                    Context context = new ContextBase(availableJob);
                    try {
                        chain.execute(context);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
        );
    }

    private List<Map<String, Object>> getAvailableJobs() {
        return tXfBillJobDao.selectMaps(
                new QueryWrapper<TXfBillJobEntity>()
                        .lambda()
                        .eq(TXfBillJobEntity::getJobType, EPD_BILL_JOB.getJobType())
                        .ne(TXfBillJobEntity::getJobStatus, BillJobStatusEnum.DONE.getJobStatus())
                        .eq(TXfBillJobEntity::getJobLockStatus, UNLOCKED.getLockStatus())
        );
    }
}
