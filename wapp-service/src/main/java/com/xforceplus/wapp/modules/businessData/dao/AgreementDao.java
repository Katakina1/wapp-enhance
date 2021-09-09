package com.xforceplus.wapp.modules.businessData.dao;

import com.xforceplus.wapp.modules.businessData.entity.AgreementEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AgreementDao {
    /**
     * 获取协议信息
     */
    List<AgreementEntity> getAgreementList(@Param("map") Map<String, Object> params);
    /**
     * 查询有多少条信息
     */
    Integer agreementQueryCount(@Param("map")Map<String, Object> map);
    /**
     * 查询有多少条红冲协议信息
     */
    Integer agreementQueryRedCount(@Param("map")Map<String, Object> map);
    /**
     * 红冲协议信息
     */
    Integer redRushAgreement(@Param("map")AgreementEntity map,@Param("userCode")String userCode,@Param("redTicketNumber")String redTicketNumber);

}
