package com.xforceplus.wapp.modules.certification.dao;

import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;


/**
 * 扫码勾选
 * @author .kevin.wang
 * @date 4/16/2018
 * 
 */
@Mapper
public interface ScanCheckDao {

    InvoiceCertificationEntity selectCheck(@Param("schemaLabel") String schemaLabel, @Param("map")Map<String, Object> map);

    Integer selectCheckTaxAccess(@Param("schemaLabel") String schemaLabel, @Param("map")Map<String, Object> map);
    
    Integer scanCheck(@Param("schemaLabel") String schemaLabel,@Param("id")String id,@Param("loginName")String loginName,@Param("userName")String userName);


}
