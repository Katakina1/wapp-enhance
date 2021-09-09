package com.xforceplus.wapp.modules.certification.service;


import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;

import java.util.Map;

/**
 * 扫码认证
 * @author kevin.wang
 * @date 4/16/2018
 */
public interface ScanCertificationService {
    
    InvoiceCertificationEntity selectCheck(String schemaLabel,Map<String, Object> map);

    Boolean scanCertification(String schemaLabel,String ids,String loginName,String userName);
    
}
