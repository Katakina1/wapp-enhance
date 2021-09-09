package com.xforceplus.wapp.modules.certification.service.impl;

import com.xforceplus.wapp.modules.certification.dao.EnterpriseTaxInformationDao;
import com.xforceplus.wapp.modules.certification.entity.EnterpriseTaxInformationEntity;
import com.xforceplus.wapp.modules.certification.service.EnterpriseTaxInformationService;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class EnterpriseTaxInformationImpl implements EnterpriseTaxInformationService {

    private EnterpriseTaxInformationDao enterpriseTaxInformationDao;

    @Autowired
    public EnterpriseTaxInformationImpl(EnterpriseTaxInformationDao enterpriseTaxInformationDao) {
        this.enterpriseTaxInformationDao = enterpriseTaxInformationDao;
    }


    /**
     * 企业税务信息
     * @param map 查询条件
     * @param schemaLabel mycat分库参数
     * @return 企业税务页面数据集
     */
    @Override
    public List<EnterpriseTaxInformationEntity> queryList(String schemaLabel,Map<String, Object> map) {
        return enterpriseTaxInformationDao.queryList(schemaLabel,map);
    }

    /**
     * 企业税务信息
     * @param map 查询条件
     * @param schemaLabel mycat分库参数
     * @return int 数据条数
     */
    @Override
    public int queryTotal(String schemaLabel,Map<String, Object> map) {
        return enterpriseTaxInformationDao.queryTotal(schemaLabel,map);
    }

    /**
     * 企业税务信息导出
     * @param map 查询条件
     * @param schemaLabel mycat分库参数
     * @return 企业税务页面数据集
     */
    @Override
    public List<EnterpriseTaxInformationEntity> queryListAll(String schemaLabel,Map<String, Object> map) {
        return enterpriseTaxInformationDao.queryListAll(schemaLabel,map);
    }

    


}
