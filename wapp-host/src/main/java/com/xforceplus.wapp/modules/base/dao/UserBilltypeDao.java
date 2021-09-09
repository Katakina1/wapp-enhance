package com.xforceplus.wapp.modules.base.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.xforceplus.wapp.modules.base.entity.UserBilltypeEntity;

/**
 * Created by Daily.zhang on 2018/04/19.
 */
@Mapper
public interface UserBilltypeDao{
   
    List<UserBilltypeEntity> getOrgDetailBill(@Param("schemaLabel") String schemaLabel, @Param("entity") UserBilltypeEntity userBilltypeEntity);

}
