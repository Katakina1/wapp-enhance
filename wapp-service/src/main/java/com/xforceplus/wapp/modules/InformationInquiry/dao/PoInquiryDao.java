package com.xforceplus.wapp.modules.InformationInquiry.dao;


import com.xforceplus.wapp.modules.InformationInquiry.entity.poEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.poExcelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PoInquiryDao {

    /**
     * 查询订单信息
     * @param map
     * @return
     */
    List<poEntity> polist(@Param("map") Map<String, Object> map);

    /**
     * 查询订单信息条数
     * @param map
     * @return
     */
    Integer polistCount(@Param("map") Map<String, Object> map);

    List<poExcelEntity> poExcellist(@Param("map")Map<String,Object> map);
}
