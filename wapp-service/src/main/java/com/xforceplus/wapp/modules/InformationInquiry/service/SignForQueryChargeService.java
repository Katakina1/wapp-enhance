package com.xforceplus.wapp.modules.InformationInquiry.service;

import com.xforceplus.wapp.modules.InformationInquiry.entity.SignForQueryEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SignForQueryExcel1Entity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SignForQueryExcelEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
public interface SignForQueryChargeService {
    /**
     * 分页列表
     * @param map
     * @return
     */
    List<SignForQueryEntity> queryList(@Param("map") Map<String, Object> map);
    /**
     * 发票查询条数
     * @param map
     * @return
     */
    Integer invoiceMatchCount(@Param("map") Map<String, Object> map);

    /**
     * 导出明细-所有数据列表(不分页)
     * @param map
     * @return
     */
    List<SignForQueryExcel1Entity> queryListAll(Map<String, Object> map);
}
