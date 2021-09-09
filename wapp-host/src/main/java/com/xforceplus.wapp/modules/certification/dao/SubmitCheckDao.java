package com.xforceplus.wapp.modules.certification.dao;

import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 勾选发票确认
 * @author .kevin.wang
 * @date 4/12/2018
 * 
 */
@Mapper
public interface SubmitCheckDao {
    
    Integer submitCheck(@Param("schemaLabel") String schemaLabel, @Param("id")String id,@Param("loginName")String loginName,@Param("userName")String userName,@Param("rzhBelongDate")String rzhBelongDate);

    Integer cancelCheck(@Param("schemaLabel") String schemaLabel,@Param("id")String id);
    List<InvoiceCertificationEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map")Map<String, Object> map);

    int queryTotal( @Param("schemaLabel") String schemaLabel,@Param("map")Map<String, Object> map);

}
