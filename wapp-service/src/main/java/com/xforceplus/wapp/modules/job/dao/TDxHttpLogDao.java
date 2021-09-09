package com.xforceplus.wapp.modules.job.dao;

import com.xforceplus.wapp.modules.job.entity.TDxHttpLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TDxHttpLogDao {

     int insertHttpLog(@Param("httpLog") TDxHttpLog httpLog,@Param("linkName")String linkName);

    TDxHttpLog selectHttpLogById(@Param("httpLogId") Long httpLogId,@Param("linkName")String linkName);

    void updateHttoLogById(@Param("tDxHttpLog") TDxHttpLog tDxHttpLog,@Param("linkName")String linkName);

    List<TDxHttpLog> getNotParseHttpLogs(@Param("type") String type,@Param("linkName")String linkName);
}