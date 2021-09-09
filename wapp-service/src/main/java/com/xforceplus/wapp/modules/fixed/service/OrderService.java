package com.xforceplus.wapp.modules.fixed.service;

import com.xforceplus.wapp.modules.fixed.entity.OrderEntity;

import java.util.List;
import java.util.Map;

public interface OrderService {
    /**
     * 查询订单
     * @param map
     * @return
     */
    List<OrderEntity> findOrderList(Map<String, Object> map);

    /**
     * 查询总数
     * @param map
     * @return
     */
    Integer countOrders(Map<String, Object> map);
}
