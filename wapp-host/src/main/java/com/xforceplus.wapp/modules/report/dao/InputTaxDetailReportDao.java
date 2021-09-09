package com.xforceplus.wapp.modules.report.dao;

import com.xforceplus.wapp.modules.report.entity.RateAmountEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 进项税额明细
 */
@Mapper
public interface InputTaxDetailReportDao {
    /**
     * 获取没有明细的发票数量
     * @param map
     * @return
     */
    Integer getNoneDetailCount(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 获取各税率对应的金额,税额
     * @param map
     * @return
     */
    List<RateAmountEntity> getRateData(@Param("schemaLabel") String schemaLabel, @Param("map")Map<String, Object> map);
}
