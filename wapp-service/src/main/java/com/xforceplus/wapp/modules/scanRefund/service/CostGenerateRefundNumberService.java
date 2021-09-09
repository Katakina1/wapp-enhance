package com.xforceplus.wapp.modules.scanRefund.service;



import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GenerateRefundNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GenerateRefundNumberExcelEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
public interface CostGenerateRefundNumberService {
    /**
     * 分页列表
     * @param map
     * @return
     */
    List<GenerateRefundNumberEntity> queryList(String schemaLabel, Map<String, Object> map);

    /**
     * 生成退单号
     *
     * @param id,rebateNo
     */
    void  rebatenobyId(String schemaLabel, Long id, String rebateNo);
    //查询退单号
    GenerateRefundNumberEntity  queryrebateno(Long id);

    void  rebatenobyuuid(String uuid);
    //查询uuid
    GenerateRefundNumberEntity  queryuuid1(Long id);

    //查询最大退单号
    GenerateRefundNumberEntity  querymaxrebateno();
    /**
     * 列表合计
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(String schemaLabel, Map<String, Object> map);

    /**
     * 导出明细-所有数据列表(不分页)
     * @param map
     * @return
     */
    List<GenerateRefundNumberEntity> queryListAll(String schemaLabel, Map<String, Object> map);

    List<GenerateRefundNumberExcelEntity> queryListAlls(String schemaLabel,Map<String, Object> map);

    List<GenerateRefundNumberEntity> epsDetaList(String schemaLabel, Map<String, Object> map);

    //eps单号查询id
    List<GenerateRefundNumberEntity> queryepsno(String epsNo);




}
