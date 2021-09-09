package com.xforceplus.wapp.modules.InformationInquiry.dao;

import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PaymentDetailEntityDao {

    /**
     * 查询
     * @param map
     * @return
     */
    List<PaymentDetailEntity> findPayList(@Param("map") Map<String, Object> map);

    /**
     * 查询订单信息条数
     * @param map
     * @return
     */
    Integer paylistCount(@Param("map") Map<String, Object> map);

}