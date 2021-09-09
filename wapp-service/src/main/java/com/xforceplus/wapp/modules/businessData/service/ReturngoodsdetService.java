package com.xforceplus.wapp.modules.businessData.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.businessData.entity.ReturngoodsdetEntity;

import java.util.List;
import java.util.Map;

public interface ReturngoodsdetService {
    /**
     * 获取退货明细信息
     * @param map 参数
     * @return
     */
    List<ReturngoodsdetEntity> getReturnGoodsdetList(Map<String, Object> map);
    /**
     * 查询有多少条信息
     */
    Integer returnGoodsDetQueryCount(Map<String, Object> map);
}
