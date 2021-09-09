package com.xforceplus.wapp.modules.businessData.dao;

import com.xforceplus.wapp.modules.businessData.entity.ReturngoodsdetEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface ReturngoodsdetDao {
    /**
     * 获取退货明细信息
     */
    List<ReturngoodsdetEntity> getReturnGoodsDetList(@Param("map") Map<String, Object> params);
    /**
     * 查询有多少条信息
     */
    Integer returnGoodsDetQueryCount(@Param("map")Map<String, Object> map);
}
