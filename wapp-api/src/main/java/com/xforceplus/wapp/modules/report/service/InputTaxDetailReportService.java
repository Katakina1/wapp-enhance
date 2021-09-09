package com.xforceplus.wapp.modules.report.service;

import com.xforceplus.wapp.modules.report.entity.RateAmountEntity;

import java.util.List;
import java.util.Map;

public interface InputTaxDetailReportService {
    /**
     * 获取没有明细的发票数量
     * @param map
     * @return
     */
    Integer getNoneDetailCount(String schemaLabel,Map<String, Object> map);

    /**
     * 获取各税率对应的金额,税额
     * @param map
     * @return
     */
    List<RateAmountEntity> getRateData(String schemaLabel, Map<String, Object> map);
}
