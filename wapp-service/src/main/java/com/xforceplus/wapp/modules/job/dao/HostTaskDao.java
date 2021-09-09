package com.xforceplus.wapp.modules.job.dao;

import com.xforceplus.wapp.modules.job.entity.HostTaskEntity;
import com.xforceplus.wapp.modules.posuopei.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.posuopei.entity.MatchEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface HostTaskDao {

    List<HostTaskEntity> getJobList(Map<String,Object> params);
    Integer getJobCount(Map<String,Object> params);

    List<MatchEntity> getMatchEntityLists(Map<String,Object> params);
    Integer getMatchEntityCount(Map<String,Object> params);
    List<InvoiceEntity> queryInvoiceList(String matchno);
}
