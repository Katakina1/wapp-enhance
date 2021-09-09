package com.xforceplus.wapp.modules.scanRefund.service;

import com.xforceplus.wapp.modules.scanRefund.entity.RebatenoForQueryExcelEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.RebatenoForQueryXiaoEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.RebatenoForQueryXiaoExcelEntity;
import org.apache.ibatis.annotations.Param;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
public interface RebatenoForQueryXiaoService {
    /**
     * 分页列表
     * @param map
     * @return
     */
    List<RebatenoForQueryXiaoEntity> queryList(@Param("map") Map<String, Object> map);
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
    List<RebatenoForQueryXiaoEntity> queryListAll(Map<String, Object> map);

    List<RebatenoForQueryXiaoExcelEntity> transformExcle(List<RebatenoForQueryXiaoEntity> invoiceEntityList) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
}
