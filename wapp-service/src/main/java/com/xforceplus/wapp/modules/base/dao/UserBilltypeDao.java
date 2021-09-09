package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.UserBilltypeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/19.
 */
@Mapper
public interface UserBilltypeDao {
    /**
     * 保存用户业务类型关联信息
     * @param t
     */
    int save(@Param("schemaLabel") String schemaLabel, @Param("entity") UserBilltypeEntity t);

    /**
     * 更新用户业务类型关联信息
     * @param t
     * @return
     */
    int update(@Param("schemaLabel") String schemaLabel, @Param("entity") UserBilltypeEntity t);
    
    /**
     * 更新用户业务类型关联信息，在设置默认之前，先把之前的设置为非默认
     * @param t
     * @return
     */
    int updateSome(@Param("schemaLabel") String schemaLabel, @Param("entity") UserBilltypeEntity t);

    /**
     * 根据id删除用户信息
     * @param id
     * @return
     */
    int delete(@Param("schemaLabel") String schemaLabel, @Param("id") Long id);

    /**
     * 删除 - 根据用户
     */
    void deleteByUserId(@Param("schemaLabel") String schemaLabel, @Param("userId") Long userId);

    /**
     * 根据条件查询用户业务类型信息
     * @param query
     * @return
     */
    List<UserBilltypeEntity> queryListBill(@Param("schemaLabel") String schemaLabel, @Param("entity") UserBilltypeEntity query);

    /**
     * 根据条件查询用户业务类型信息总数
     * @param query
     * @return
     */
    int queryTotal(@Param("schemaLabel") String schemaLabel, @Param("entity") UserBilltypeEntity query);

    List<UserBilltypeEntity> getNotAddListBill(@Param("schemaLabel") String schemaLabel, @Param("billtypeidArr") String[] billtypeidArr, @Param("userBilltypeEntity") UserBilltypeEntity userBilltypeEntity, @Param("orgType") String orgType, @Param("orgChildArr") String[] orgChildArr);

    int getNotAddListTotalBill(@Param("schemaLabel") String schemaLabel, @Param("billtypeidArr") String[] billtypeidArr, @Param("userBilltypeEntity") UserBilltypeEntity userBilltypeEntity, @Param("orgType") String orgType, @Param("orgChildArr") String[] orgChildArr);

    List<UserBilltypeEntity> getOrgDetailBill(@Param("schemaLabel") String schemaLabel, @Param("entity") UserBilltypeEntity userBilltypeEntity);

    int getOrgDetailCountBill(@Param("schemaLabel") String schemaLabel, @Param("entity") UserBilltypeEntity userBilltypeEntity);
    int queryListBillCount(@Param("schemaLabel") String schemaLabel, @Param("entity") UserBilltypeEntity entity);

}
