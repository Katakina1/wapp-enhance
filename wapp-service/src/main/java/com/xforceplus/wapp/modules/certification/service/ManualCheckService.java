package com.xforceplus.wapp.modules.certification.service;


import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;

import java.util.List;
import java.util.Map;


/**
 * 手工勾选
 * @author kevin.wang
 * @date 4/14/2018
 */
public interface ManualCheckService {

    /**
     * 手工勾选
     * @param map 查询条件
     * @param schemaLabel mycat分库参数
     * @return 可勾选操作的数据集
     */
    List<InvoiceCertificationEntity> queryList(String schemaLabel,Map<String, Object> map);

    /**
     * 手工勾选
     * @param map 查询条件
     * @param schemaLabel mycat分库参数
     * @return 可勾选操作的数据数
     */
    ReportStatisticsEntity queryTotal(String schemaLabel, Map<String, Object> map);

    /**
     * 手工勾选
     * @param ids 勾选的ID
     * @param schemaLabel mycat分库参数
     * @return 可勾选操作的数据集
     */
    Boolean manualCheck(String schemaLabel,String ids,String loginName,String userName);
}
