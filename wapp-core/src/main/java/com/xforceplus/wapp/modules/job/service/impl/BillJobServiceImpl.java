package com.xforceplus.wapp.modules.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.modules.job.service.BillJobService;
import com.xforceplus.wapp.repository.dao.TXfBillJobDao;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.math.NumberUtils;
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
                        .orderByAsc(TXfBillJobEntity::getCreateTime)
        );
    }

    /**
     * 保存chain context瞬时状态入库
     *
     * @param context
     * @return
     */
    @Override
    public boolean saveContext(Context context) {
        TXfBillJobEntity tXfBillJobEntity = new TXfBillJobEntity();
        tXfBillJobEntity.setId(Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.ID))));
        tXfBillJobEntity.setJobStatus(Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_STATUS))));
        tXfBillJobEntity.setRemark(String.valueOf(context.get(TXfBillJobEntity.REMARK)));
        if (NumberUtils.isNumber(String.valueOf(context.get(TXfBillJobEntity.JOB_ACQUISITION_OBJECT)))) {
            tXfBillJobEntity.setJobAcquisitionObject(Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_ACQUISITION_OBJECT))));
        }
        if (NumberUtils.isNumber(String.valueOf(context.get(TXfBillJobEntity.JOB_ENTRY_OBJECT)))) {
            tXfBillJobEntity.setJobEntryObject(Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_ENTRY_OBJECT))));
        }
        if (NumberUtils.isNumber(String.valueOf(context.get(TXfBillJobEntity.JOB_ENTRY_PROGRESS)))) {
            tXfBillJobEntity.setJobEntryProgress(Long.parseLong(String.valueOf(context.get(TXfBillJobEntity.JOB_ENTRY_PROGRESS))));
        }
        return updateById(tXfBillJobEntity);
    }
}
