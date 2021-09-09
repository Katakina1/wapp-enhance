package com.xforceplus.wapp.modules.base.service;


import com.xforceplus.wapp.modules.base.entity.UserBilltypeEntity;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/19.
 */
public interface UserBilltypeService {
    /**
     * 保存
     *
     * @param entity
     */
    void save(String schemaLabel, UserBilltypeEntity entity);

    /**
     * 修改
     */
    void update(String schemaLabel, UserBilltypeEntity entity);
    
    /**
     * 修改
     */
    void updateSome(String schemaLabel, UserBilltypeEntity entity);

    /**
     * 删除
     */
    void delete(String schemaLabel, Long id);

    /**
     * 删除 - 根据用户
     */
    void deleteByUserId(String schemaLabel, Long userId);

    /**
     * 根据条件查询
     */
    List<UserBilltypeEntity> queryList(String schemaLabel, UserBilltypeEntity entity);
    

    /**
     * 查询总数
     */
    int queryTotal(String schemaLabel, UserBilltypeEntity entity);
    
    /**
     * 
     */
    List<UserBilltypeEntity> getOrgDetail(String schemaLabel, UserBilltypeEntity userBilltypeEntity);
    
    /**
     * 
     */
    int getOrgDetailCount(String schemaLabel, UserBilltypeEntity userBilltypeEntity);
    
    /**
     * 
     */
    int queryListBillCount(String schemaLabel, UserBilltypeEntity entity);
    
    /**
     *  
     */
    List<UserBilltypeEntity> getNotAddList(String schemaLabel, String[] billtypeidArr, UserBilltypeEntity userBilltypeEntity, String orgType, String[] orgChildArr);

    /**
     * 根据company(所属中心企业),获取还未添加的机构数
     */
    int getNotAddListTotal(String schemaLabel, String[] billtypeidArr, UserBilltypeEntity userBilltypeEntity, String orgType, String[] orgChildArr);

}
