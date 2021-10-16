package com.xforceplus.wapp.modules.backFill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.repository.dao.TXfElecUploadRecordDetailDao;
import com.xforceplus.wapp.repository.entity.TXfElecUploadRecordDetailEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElectronicUploadRecordDetailService {

    @Autowired
    private TXfElecUploadRecordDetailDao electronicUploadRecordDetailDao;

    /**
     * 通过验真任务ID获取上传详情
     *
     * @param taskId 验真任务ID
     * @return
     */
    public TXfElecUploadRecordDetailEntity getByVerifyTaskId(String taskId) {
        QueryWrapper<TXfElecUploadRecordDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("xf_verify_task_id",taskId);
        return this.electronicUploadRecordDetailDao.selectOne(wrapper);
    }

    /**
     * 通过云识别任务ID获取上传详情
     *
     * @param taskId 云识别ID
     * @return
     */
    public TXfElecUploadRecordDetailEntity getByDiscernTaskId(String taskId) {
        QueryWrapper<TXfElecUploadRecordDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("xf_discern_task_id",taskId);
        return this.electronicUploadRecordDetailDao.selectOne(wrapper);
    }

    public void updateById(TXfElecUploadRecordDetailEntity entity) {
        electronicUploadRecordDetailDao.updateById(entity);
    }
}
