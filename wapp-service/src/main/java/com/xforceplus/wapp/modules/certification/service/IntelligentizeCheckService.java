package com.xforceplus.wapp.modules.certification.service;


import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;

import java.util.List;
import java.util.Map;


/**
 * 手工勾选
 * @author kevin.wang
 * @date 4/14/2018
 */
public interface IntelligentizeCheckService {
    
    List<InvoiceCertificationEntity> queryList(String schemaLabel,Map<String, Object> map,String loginName,String userName);

    InvoiceCertificationEntity selectQueryList(String schemaLabel,Map<String, Object> map);

    int queryTotal(String schemaLabel,Map<String, Object> map);


    String selectSwitchStatus();
}
