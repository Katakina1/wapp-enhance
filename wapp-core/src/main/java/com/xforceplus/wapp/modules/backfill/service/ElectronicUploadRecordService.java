package com.xforceplus.wapp.modules.backfill.service;

import com.xforceplus.wapp.repository.dao.TXfElecUploadRecordDetailDao;
import com.xforceplus.wapp.repository.daoExt.ElectronicUploadRecordDao;
import com.xforceplus.wapp.repository.entity.TXfElecUploadRecordEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElectronicUploadRecordService {

    @Autowired
    private ElectronicUploadRecordDao electronicUploadRecordDao;


    public void increaseFailure(String batchNo) {
        int num = electronicUploadRecordDao.countNum(batchNo, "0");
        electronicUploadRecordDao.increaseFailureNum(batchNo, num);
    }


    public void increaseFailure(String batchNo, int num) {
        electronicUploadRecordDao.increaseFailureSpecialNum(batchNo,num);
    }


    public void increaseSucceed(String batchNo) {
        int num = electronicUploadRecordDao.countNum(batchNo, "1");
        electronicUploadRecordDao.increaseSucceedNum(batchNo, num);
    }

    public TXfElecUploadRecordEntity getCompleteByBatchNo(String batchNo) {
        return electronicUploadRecordDao.selectCompletedByBatchNo(batchNo);
    }

    public TXfElecUploadRecordEntity getByBatchNo(String batchNo) {
        return electronicUploadRecordDao.selectByBatchNo(batchNo);
    }
}
