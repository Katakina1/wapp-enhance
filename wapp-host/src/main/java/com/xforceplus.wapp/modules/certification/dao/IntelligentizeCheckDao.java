package com.xforceplus.wapp.modules.certification.dao;

import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 智能勾选
 * @author .kevin.wang
 * @date 4/19/2018
 * 
 */
@Mapper
public interface IntelligentizeCheckDao {
    /**
     * 勾选操作
     */
    Integer intelligentizeCheck(@Param("schemaLabel") String schemaLabel, @Param("id")Long id,@Param("loginName")String loginName,@Param("userName")String userName);

    /**
     * 获取所有符合条件的id
     */
    InvoiceCertificationEntity selectCheck (@Param("schemaLabel") String schemaLabel,@Param("id")String id);

    List<InvoiceCertificationEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map")Map<String, Object> map);

    int queryTotal(@org.apache.ibatis.annotations.Param("schemaLabel") String schemaLabel, @Param("map")Map<String, Object> map);
    
}
