package com.xforceplus.wapp.modules.report.dao;


import com.xforceplus.wapp.modules.report.entity.QuestionInvoiceQuantityAndRatioEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SupplierIssueInvoiceQuantityandRatioDao {
    /**
     * 查询问题发票数量及比率
     * @param map
     * @return
     */
    List<QuestionInvoiceQuantityAndRatioEntity> problemInvoice(@Param("map") Map<String, Object> map);
}
