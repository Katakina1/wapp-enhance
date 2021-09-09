package com.xforceplus.wapp.modules.certification.dao;

import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.sys.dao.SysBaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;


/**
 * 手工认证
 * @author .kevin.wang
 * @date 4/12/2018
 * 
 */
@Mapper
public interface ScanCertificationDao extends SysBaseDao<InvoiceCertificationEntity> {

    InvoiceCertificationEntity selectCheck(@Param("schemaLabel") String schemaLabel, @Param("map")Map<String, Object> map);

    Integer selectCheckTaxAccess(@Param("schemaLabel") String schemaLabel, @Param("map")Map<String, Object> map);

    Integer scanCertification(@Param("schemaLabel") String schemaLabel,@Param("id")String id,@Param("loginName")String loginName,@Param("userName")String userName,@Param("rzhBelongDate")String rzhBelongDate);


}
