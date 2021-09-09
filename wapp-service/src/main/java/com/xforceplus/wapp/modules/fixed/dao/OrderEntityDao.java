package com.xforceplus.wapp.modules.fixed.dao;

import com.xforceplus.wapp.modules.fixed.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderEntityDao {
    /**
     * 查询订单
     * @param map
     * @return
     */
    List<OrderEntity> findOrderList(@Param("map") Map<String, Object> map);

    /**
     * 查询总数
     * @param map
     * @return
     */
    Integer countOrders(@Param("map") Map<String, Object> map);
}
