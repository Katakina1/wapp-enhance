package com.xforceplus.wapp.modules.backFill.service;

import com.xforceplus.wapp.repository.daoExt.ElectronicUploadRecordDao;
import com.xforceplus.wapp.repository.entity.TXfElecUploadRecordEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElectronicUploadRecordService {

    @Autowired
    private ElectronicUploadRecordDao electronicUploadRecordDao;


    public void increaseFailure(String batchNo) {
        electronicUploadRecordDao.increaseFailureNum(batchNo);
    }


    public void increaseFailure(String batchNo, int num) {
        electronicUploadRecordDao.increaseFailureSpecialNum(batchNo,num);
    }


    public void increaseSucceed(String batchNo) {
        electronicUploadRecordDao.increaseSucceedNum(batchNo);
    }

    public TXfElecUploadRecordEntity getCompleteByBatchNo(String batchNo) {
        return electronicUploadRecordDao.selectCompletedByBatchNo(batchNo);
    }

    public TXfElecUploadRecordEntity getByBatchNo(String batchNo) {
        return electronicUploadRecordDao.selectByBatchNo(batchNo);
    }
}
