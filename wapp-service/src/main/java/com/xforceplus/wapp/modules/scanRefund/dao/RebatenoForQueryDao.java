package com.xforceplus.wapp.modules.scanRefund.dao;

import com.xforceplus.wapp.modules.scanRefund.entity.RebatenoForQueryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
@Mapper
public interface RebatenoForQueryDao {

    /**
     * 查询所有数据
     * @param map
     * @return
     */
    List<RebatenoForQueryEntity> queryList(@Param("map") Map<String, Object> map);

    /**
     * 发票查询条数
     * @param map
     * @return
     */
    Integer invoiceMatchCount(@Param("map") Map<String, Object> map);
    /**
     * 查询所有数据(不分页)
     * @param map
     * @return
     */
    List<RebatenoForQueryEntity> queryListAll(@Param("map") Map<String, Object> map);



}
