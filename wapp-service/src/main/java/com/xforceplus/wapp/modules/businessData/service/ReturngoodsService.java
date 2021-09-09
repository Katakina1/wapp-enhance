package com.xforceplus.wapp.modules.businessData.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.businessData.entity.ReturngoodsEntity;

import java.util.List;
import java.util.Map;

public interface ReturngoodsService {
    /**
     * 获取退货信息
     * @param map 参数
     * @return
     */
    List<ReturngoodsEntity> getReturnGoodsList(Map<String, Object> map);
    /**
     * 查询有多少条信息
     */
    Integer returnGoodsQueryCount(Map<String, Object> map);
    /**
     * 获取未红冲退货信息
     * @param map
     * @return
     */
    List<ReturngoodsEntity> getReturnGoodsListBy(Map<String, Object> map);
    /**
     * 查询有多少条未红冲退货信息
     */
    Integer returnGoodsQueryRedCount(Map<String, Object> map);
}
