package com.xforceplus.wapp.modules.job.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.repository.dao.TXfBillJobDao;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.enums.BillJobLockStatusEnum.UNLOCKED;
import static com.xforceplus.wapp.enums.BillJobTypeEnum.AGREEMENT_BILL_JOB;

/**
 * @program: wapp-generator
 * @description: bill job service
 * @author: Kenny Wong
 * @create: 2021-10-14 16:01
 **/
@Service
public class BillJobService {

    @Autowired
    private TXfBillJobDao tXfBillJobDao;

    /**
     * 获取待执行的任务
     *
     * @return
     */
    public List<Map<String, Object>> obtainAvailableJobs() {
        return tXfBillJobDao.selectMaps(
                new QueryWrapper<TXfBillJobEntity>()
                        .lambda()
                        .eq(TXfBillJobEntity::getJobType, AGREEMENT_BILL_JOB.getJobType())
                        .ne(TXfBillJobEntity::getJobStatus, BillJobStatusEnum.DONE.getJobStatus())
                        .eq(TXfBillJobEntity::getJobLockStatus, UNLOCKED.getLockStatus())
        );
    }

    public int updateStatus(Integer id, int status) {
        TXfBillJobEntity tXfBillJobEntity = new TXfBillJobEntity();
        tXfBillJobEntity.setId(id);
        tXfBillJobEntity.setJobStatus(status);
        return updateById(tXfBillJobEntity);
    }

    public int updateById(TXfBillJobEntity tXfBillJobEntity) {
        tXfBillJobEntity.setUpdateTime(new Date());
        return tXfBillJobDao.updateById(tXfBillJobEntity);
    }

    /**
     * 根据job id锁定任务
     *
     * @param id
     * @return
     */
    public int lockJob(Integer id) {
        return lockJob(id, true);
    }

    /**
     * 根据job id解锁任务
     *
     * @param id
     * @return
     */
    public int unlockJob(Integer id) {
        return lockJob(id, false);
    }

    /**
     * 根据job id锁定/解锁任务
     *
     * @param id
     * @param lockStatus false-unlocked, true-locked
     * @return
     */
    private int lockJob(Integer id, boolean lockStatus) {
        TXfBillJobEntity tXfBillJobEntity = new TXfBillJobEntity();
        tXfBillJobEntity.setId(id);
        tXfBillJobEntity.setJobLockStatus(lockStatus);
        tXfBillJobEntity.setUpdateTime(new Date());
        return tXfBillJobDao.updateById(tXfBillJobEntity);
    }
}
