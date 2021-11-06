package com.xforceplus.wapp.modules.job.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import org.apache.commons.chain.Context;

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
     * 获取待执行的任务
     *
     * @return
     */
    List<Map<String, Object>> obtainAvailableJobs(int JobType);

    /**
     *
     * @param context
     * @return
     */
    boolean saveContext(Context context);

}
