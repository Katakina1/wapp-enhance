package com.xforceplus.wapp.modules.report.service;

import com.xforceplus.wapp.modules.report.entity.DailyReportEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 进项税额报表
 */
public interface InputTaxReportService {
    /**
     * 获取进项总金额
     * @param map
     * @return
     */
    Double getTotalAmount(String schemaLabel,Map<String, Object> map);

    /**
     * 获取进项总税额
     * @param map
     * @return
     */
    Double getTotalTax(String schemaLabel,Map<String, Object> map);

    /**
     * 获取转出总税额
     * @param map
     * @return
     */
    Double getTotalOutTax(String schemaLabel,Map<String, Object> map);

    /**
     * 获取转出税额明细
     * @param map
     * @return
     */
    List<DailyReportEntity> getOutTaxDetail(String schemaLabel, Map<String, Object> map);
}
