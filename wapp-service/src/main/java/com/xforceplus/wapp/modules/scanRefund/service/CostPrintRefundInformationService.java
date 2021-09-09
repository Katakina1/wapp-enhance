package com.xforceplus.wapp.modules.scanRefund.service;



import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberExcelEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
public interface CostPrintRefundInformationService {
    /**
     * 分页列表
     * @param map
     * @return
     */
    List<EnterPackageNumberEntity> queryList(String schemaLabel, Map<String, Object> map);

    /**
     * 列表合计
     * @param map
     * @return
     */
    ReportStatisticsEntity queryTotalResult(String schemaLabel, Map<String, Object> map);

    /**
     * 查询退单发票
     * @param id
     * @return
     */
//    EnterPackageNumberEntity queryRefundListAll(Long id);
    EnterPackageNumberEntity queryRefundList(Long id);

    void exportPoPdf(Map<String,Object> params, HttpServletResponse response);

    /**
     * 导出明细-所有数据列表(不分页)
     * @param map
     * @return
     */
    List<EnterPackageNumberEntity> queryListAll(Map<String, Object> map);

    EnterPackageNumberEntity queryPostType(Long id);

    List<EnterPackageNumberExcelEntity> queryListForExcel(String schemaLabel, Map<String,Object> params);
}
