package com.xforceplus.wapp.modules.businessData.dao;

import com.xforceplus.wapp.modules.businessData.entity.ReturngoodsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface ReturngoodsDao {
    /**
     * 获取退货信息
     */
    List<ReturngoodsEntity> getReturnGoodsList(@Param("map") Map<String, Object> params);
    /**
     * 查询有多少条信息
     */
    Integer returnGoodsQueryCount(@Param("map")Map<String, Object> map);
    /**
     * 查询有多少条红冲退货信息
     */
    Integer returnGoodsQueryRedCount(@Param("map")Map<String, Object> map);
    /**
     * 红冲退货信息
     */
    Integer redRushreturnGoods(@Param("map")ReturngoodsEntity map,@Param("userCode")String userCode,@Param("redTicketNumber")String redTicketNumber);
}
