package com.xforceplus.wapp.modules.certification.service;


import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;

import java.util.Map;

/**
 * 扫码勾选
 * @author kevin.wang
 * @date 4/14/2018
 */
public interface ScanCheckService {


    InvoiceCertificationEntity selectCheck(String schemaLabel,Map<String, Object> map);
    
    Boolean scanCheck(String schemaLabel,String ids,String loginName,String userName);

}
