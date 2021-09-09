package com.xforceplus.wapp.modules.certification.service;


import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationExcelEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;

import java.util.List;
import java.util.Map;

/**
 * 手工认证
 * @author kevin.wang
 * @date 4/14/2018
 */
public interface ManualCertificationService {
    
    List<InvoiceCertificationEntity> queryList(String schemaLabel,Map<String, Object> map);

    ReportStatisticsEntity queryTotal(String schemaLabel, Map<String, Object> map);

    String manualCertification(String schemaLabel,String ids,String loginName,String userName,long userId);


    String getCurrentTaxPeriod(String schemaLabel,String id);
    
    List<InvoiceCertificationExcelEntity> queryExportList(String schemaLabel, Map<String, Object> map);
}
