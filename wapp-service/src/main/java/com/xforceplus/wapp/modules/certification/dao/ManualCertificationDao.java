package com.xforceplus.wapp.modules.certification.dao;

import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * 手工认证
 * @author .kevin.wang
 * @date 4/12/2018
 * 
 */
@Mapper
public interface ManualCertificationDao {

    /**
     * 认证处理
     * @param schemaLabel,id
     * @return  Integer
     */
    Integer manualCertification(@Param("schemaLabel") String schemaLabel, @Param("id")String id,@Param("loginName")String loginName,@Param("userName")String userName,@Param("rzhBelongDate")String rzhBelongDate,@Param("taxAmount") BigDecimal taxAmount);

    /**
     * 查询所有数据
     * @param map
     * @return
     */
    List<InvoiceCertificationEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map")Map<String, Object> map);

    /**
     * 查询所有数据
     * @param map
     * @return  查询的条数
     */
    ReportStatisticsEntity queryTotal(@Param("schemaLabel") String schemaLabel, @Param("map")Map<String, Object> map);

    /**
     * 获取税款所属期
     * @param id
     * @return  查询的条数
     */
    String getRzhBelongDate(@Param("schemaLabel") String schemaLabel, @Param("id")String id);


    String getCurrentTaxPeriod(@Param("schemaLabel")String schemaLabel, @Param("id")String id);
    
    /**
     * 查询所有数据
     * @param map
     * @return
     */
    List<InvoiceCertificationEntity> queryExportList(@Param("schemaLabel") String schemaLabel, @Param("map")Map<String, Object> map);

    
    

}
