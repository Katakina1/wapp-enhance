package com.xforceplus.wapp.modules.redTicket.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface ReturnAgreementMiddleDao {
    /**
     * 添加退货、协议匹配中间信息
     */
    Integer insertReturnAgreementMiddle(@Param("map") Map<String, Object> map);
}
