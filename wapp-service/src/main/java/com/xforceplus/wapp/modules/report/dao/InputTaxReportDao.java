package com.xforceplus.wapp.modules.report.dao;

import com.xforceplus.wapp.modules.report.entity.DailyReportEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 进项税额报表
 */
@Mapper
public interface InputTaxReportDao {
    /**
     * 获取进项总金额
     * @param map
     * @return
     */
    Double getTotalAmount(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

    /**
     * 获取进项总税额
     * @param map
     * @return
     */
    Double getTotalTax(@Param("schemaLabel") String schemaLabel,@Param("map") Map<String, Object> map);

    /**
     * 获取转出总税额
     * @param map
     * @return
     */
    Double getTotalOutTax(@Param("schemaLabel") String schemaLabel,@Param("map") Map<String, Object> map);

    /**
     * 获取转出税额明细
     * @param map
     * @return
     */
    List<DailyReportEntity> getOutTaxDetail(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);
}
