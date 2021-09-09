package com.xforceplus.wapp.modules.certification.dao;

import com.xforceplus.wapp.modules.certification.entity.ImportCertificationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 导入认证数据层
 * @author Colin.hu
 * @date 4/20/2018
 */
@Mapper
public interface ImportCheckDao {


    /**
     * 提交勾选
     * @param entityList 提交勾选的发票信息
     * @return 结果
     */
    Integer submit(@Param("schemaLabel") String schemaLabel, @Param("list")List<ImportCertificationEntity> entityList,@Param("loginName")String loginName,@Param("userName")String userName);
}
