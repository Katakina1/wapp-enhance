package com.xforceplus.wapp.modules.InformationInquiry.dao;


import com.xforceplus.wapp.modules.InformationInquiry.entity.ClaimEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface ClaimInquiryDao {

    /**
     * 查询索赔信息
     * @param map
     * @return
     */
    List<ClaimEntity> claimlist(@Param("map")Map<String, Object> map);

    /**
     * 查询索赔信息条数
     * @param map
     * @return
     */
    Integer claimlistCount(@Param("map")Map<String, Object> map);
}
