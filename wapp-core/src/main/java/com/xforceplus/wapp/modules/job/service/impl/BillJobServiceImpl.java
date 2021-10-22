package com.xforceplus.wapp.modules.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.modules.job.service.BillJobService;
import com.xforceplus.wapp.repository.dao.TXfBillJobDao;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.enums.BillJobLockStatusEnum.UNLOCKED;

/**
 * @program: wapp-generator
 * @description: bill job service
 * @author: Kenny Wong
 * @create: 2021-10-14 16:01
 **/
@Service
public class BillJobServiceImpl extends ServiceImpl<TXfBillJobDao, TXfBillJobEntity> implements BillJobService {

    @Override
    public List<Map<String, Object>> obtainAvailableJobs(int jobType) {
        return listMaps(
                new QueryWrapper<TXfBillJobEntity>()
                        .lambda()
                        .eq(TXfBillJobEntity::getJobType, jobType)
                        .ne(TXfBillJobEntity::getJobStatus, BillJobStatusEnum.DONE.getJobStatus())
                        .eq(TXfBillJobEntity::getJobLockStatus, UNLOCKED.getLockStatus())
                        .orderByAsc(TXfBillJobEntity::getCreateTime)
        );
    }

    @Override
    public boolean lockJob(Integer jobId) {
        return lockJob(jobId, true);
    }

    @Override
    public boolean unlockJob(Integer jobId) {
        return lockJob(jobId, false);
    }

    /**
     * 根据job id锁定/解锁任务
     *
     * @param id
     * @param lockStatus false-unlocked, true-locked
     * @return
     */
    private boolean lockJob(Integer id, boolean lockStatus) {
        TXfBillJobEntity tXfBillJobEntity = new TXfBillJobEntity();
        tXfBillJobEntity.setId(id);
        tXfBillJobEntity.setJobLockStatus(lockStatus);
        tXfBillJobEntity.setUpdateTime(new Date());
        return update(tXfBillJobEntity,
                new UpdateWrapper<TXfBillJobEntity>()
                        .lambda()
                        .eq(TXfBillJobEntity::getId, id)
                        .eq(TXfBillJobEntity::getJobLockStatus, !lockStatus));
    }
}
