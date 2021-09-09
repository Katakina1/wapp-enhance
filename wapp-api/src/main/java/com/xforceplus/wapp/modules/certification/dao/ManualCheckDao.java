package com.xforceplus.wapp.modules.certification.dao;

import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 手工勾选
 * @author .kevin.wang
 * @date 4/12/2018
 * 
 */
@Mapper
public interface ManualCheckDao {

   /**
    * 手工勾选
    * @param id 
    * @param schemaLabel mycat分库参数
    * @return Integer
    */
   Integer manualCheck(@Param("schemaLabel") String schemaLabel, @Param("id")String id,@Param("loginName")String loginName,@Param("userName")String userName);

   /**
    * 手工勾选
    * @param map 查询条件
    * @param schemaLabel mycat分库参数
    * @return 可勾选操作的数据集
    */
   List<InvoiceCertificationEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map")Map<String, Object> map);

   /**
    * 手工勾选
    * @param map 查询条件
    * @param schemaLabel mycat分库参数
    * @return 可勾选操作的数据数
    */
   int queryTotal( @Param("schemaLabel") String schemaLabel,@Param("map")Map<String, Object> map);
}
