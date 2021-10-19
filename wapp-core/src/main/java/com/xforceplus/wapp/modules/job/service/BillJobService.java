package com.xforceplus.wapp.modules.job.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;

import java.util.List;
import java.util.Map;

/**
 * @program: wapp-generator
 * @description: bill job service
 * @author: Kenny Wong
 * @create: 2021-10-15 17:42
 **/
public interface BillJobService extends IService<TXfBillJobEntity> {

    /**
     * 根据job id锁定任务
     *
     * @param jobId
     * @return
     */
    boolean lockJob(Integer jobId);

    /**
     * 根据job id解锁任务
     *
     * @param jobId
     * @return
     */
    boolean unlockJob(Integer jobId);

    /**
     * 获取待执行的任务
     *
     * @return
     */
    List<Map<String, Object>> obtainAvailableJobs(int JobType);

}
