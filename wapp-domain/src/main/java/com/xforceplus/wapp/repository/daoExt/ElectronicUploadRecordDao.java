package com.xforceplus.wapp.repository.daoExt;

import com.xforceplus.wapp.repository.entity.TXfElecUploadRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ElectronicUploadRecordDao extends BaseDao<TXfElecUploadRecordEntity> {
    /**
     * 通过批次号获取已经完成的上传记录
     *
     * @param batchNo 批次号
     * @return
     */
    TXfElecUploadRecordEntity selectCompletedByBatchNo(@Param("batchNo") String batchNo);

    /**
     * 成功数量+1
     *
     * @param batchNo 批次号
     * @return
     */
    int increaseSucceedNum(@Param("batchNo") String batchNo);

    /**
     * 失败数量+1
     *
     * @param batchNo 批次号
     * @return
     */
    int increaseFailureNum(@Param("batchNo") String batchNo);
    /**
     * 失败数量+1
     *
     * @param batchNo 批次号
     * @return
     */
    int increaseFailureSpecialNum(@Param("batchNo") String batchNo, @Param("num") int num);

    TXfElecUploadRecordEntity selectByBatchNo(@Param("batchNo") String batchNo);
}
