package com.xforceplus.wapp.modules.InformationInquiry.dao;

import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/12/14 23:08
 */
@Mapper
public interface AuthenticationQueryDao {
    ReportStatisticsEntity getCertificationListCount(Map<String, Object> map);
    List<InvoiceCollectionInfo> selectCertificationList(Map<String, Object> map);
    ReportStatisticsEntity getAribaCertificationListCount(Map<String, Object> map);
    List<InvoiceCollectionInfo> selectAribaCertificationList(Map<String, Object> map);
    String getTpDateByUuid(String uuid);
    String selectStore(String jv);
    String selectCostDeptToJv(String costDept);
}
