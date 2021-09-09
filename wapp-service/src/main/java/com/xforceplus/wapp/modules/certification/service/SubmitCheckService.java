package com.xforceplus.wapp.modules.certification.service;


import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;

import java.util.List;
import java.util.Map;


/**
 * 勾选发票确认
 * @author kevin.wang
 * @date 4/14/2018
 */
public interface SubmitCheckService {
    
    List<InvoiceCertificationEntity> queryList(String schemaLabel,Map<String, Object> map);

    ReportStatisticsEntity queryTotal(String schemaLabel, Map<String, Object> map);

    Boolean submitCheck(String schemaLabel,String ids,String loginName,String userName);

    Boolean cancelCheck(String schemaLabel,String ids);
}
