package com.xforceplus.wapp.modules.certification.service;


import com.xforceplus.wapp.modules.certification.entity.EnterpriseTaxInformationEntity;

import java.util.List;
import java.util.Map;

/**
 * 企业税务信息
 * @author kevin.wang
 * @date 4/12/2018
 */
public interface EnterpriseTaxInformationService {
    /**
     * 企业税务信息
     * @param schemaLabel mycat分库参数
     * @param map 查询条件
     * @return 企业税务页面数据集
     */
    
    List<EnterpriseTaxInformationEntity> queryList(String schemaLabel,Map<String, Object> map);

    /**
     * 企业税务信息
     * @param map 查询条件
     * @param schemaLabel mycat分库参数
     * @return 企业税务页面数据集
     */
    int queryTotal(String schemaLabel,Map<String, Object> map);

    /**
     * 所有数据列表(不分页)导出用
     * @param map
     * @param schemaLabel mycat分库参数
     * @return
     */
    List <EnterpriseTaxInformationEntity> queryListAll(String schemaLabel,Map<String, Object> map);
}
