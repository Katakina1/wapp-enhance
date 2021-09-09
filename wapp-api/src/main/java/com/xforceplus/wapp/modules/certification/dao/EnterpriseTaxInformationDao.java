package com.xforceplus.wapp.modules.certification.dao;

import com.xforceplus.wapp.modules.certification.entity.EnterpriseTaxInformationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;
import java.util.Map;


/**
 * 企业税务信息
 * @author .kevin.wang
 * @date 4/12/2018
 * 
 */
@Mapper
public interface EnterpriseTaxInformationDao{

    /**
     * 查询所有数据(不分页)
     * @param map
     * @return
     */
    List<EnterpriseTaxInformationEntity> queryListAll(@Param("schemaLabel") String schemaLabel, @Param("map")Map<String, Object> map);

    /**
     * 查询所有数据
     * @param map
     * @return
     */
    List<EnterpriseTaxInformationEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map")Map<String, Object> map);

    /**
     * 查询所有数据
     * @param map
     * @return  查询的条数
     */
    int queryTotal(@Param("schemaLabel") String schemaLabel, @Param("map")Map<String, Object> map);

}
