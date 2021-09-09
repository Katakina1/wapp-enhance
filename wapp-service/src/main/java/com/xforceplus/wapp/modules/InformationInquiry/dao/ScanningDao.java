package com.xforceplus.wapp.modules.InformationInquiry.dao;

import com.xforceplus.wapp.modules.InformationInquiry.entity.ScanningEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScanningDao {
    /**
     * 查询扫描信息
     */
    List<ScanningEntity> scanningList(@Param("map") Map<String, Object> map);
    /**
     * 查询扫描信息条数
     */
    Integer scanningCount(@Param("map") Map<String, Object> map);
    /**
     * 查询当前登录人所属机构
     */
    String serachUserOrgType(@Param("map") Map<String, Object> map);
}
