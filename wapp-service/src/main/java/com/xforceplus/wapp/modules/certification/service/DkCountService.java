package com.xforceplus.wapp.modules.certification.service;


import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.certification.entity.TDxDkCountDetail;
import com.xforceplus.wapp.modules.certification.entity.TDxDkCountEntity;
import com.xforceplus.wapp.modules.certification.entity.TDxDkCountLogEntity;
import com.xforceplus.wapp.modules.job.entity.TAcOrg;
import com.xforceplus.wapp.modules.job.entity.TDxTaxCurrent;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;

import java.util.List;
import java.util.Map;


/**
 * 抵扣统计
 * @author kevin.wang
 * @date 4/14/2018
 */
public interface DkCountService {

    /**
     * 抵扣统计列表
     * @param map 查询条件
     * @param schemaLabel mycat分库参数
     * @return 可勾选操作的数据集
     */
    List<TDxDkCountEntity> queryList(String schemaLabel, Map<String, Object> map);

    /**
     * 抵扣统计
     * @param map 查询条件
     * @param schemaLabel mycat分库参数
     * @return 可勾选操作的数据数
     */
    ReportStatisticsEntity queryTotal(String schemaLabel, Map<String, Object> map);

    Integer selectBeforeTj(Map<String, Object> map);
    void insertDk(String gfsh, String skssq);
    void insertConfirm(String gfsh, String skssq,String ywmm);
    void insertCxtj(String gfsh, String skssq);
    List<TDxDkCountEntity> selectDksh(String[] gfsh);
    List<TDxDkCountEntity> checkDksh(Map<String, Object> map);
    void insertDkLog(String[] gfshs, String[] skssqs, String loginName, String operaType);
    List<TDxDkCountDetail> selectDkDetail(String gfsh, String skssq);
    TAcOrg selectUpgrad(String gfsh);


    void insertCxqs(String gfsh, String skssq);
}
