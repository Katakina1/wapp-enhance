package com.xforceplus.wapp.modules.posuopei.dao;

import com.xforceplus.wapp.modules.posuopei.entity.SubmitOutstandingReportEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SubmitOutstandingReportDao {
    Integer insertSubmitOutstandingReport(@Param("entity") SubmitOutstandingReportEntity submitOutstandingReportEntity);

    Integer getNineCount(@Param("batchId")String batchId);
    Integer deleteBatchid(@Param("batchId")String batchId);
}
