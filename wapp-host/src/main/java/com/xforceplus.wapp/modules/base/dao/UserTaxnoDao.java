package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.UserTaxnoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/19.
 */
@Mapper
public interface UserTaxnoDao{
    /**
     * 保存用户税号关联信息
     * @param t
     */
    int save(@Param("schemaLabel") String schemaLabel, @Param("entity") UserTaxnoEntity t);

    /**
     * 更新用户税号关联信息
     * @param t
     * @return
     */
    int update(@Param("schemaLabel") String schemaLabel, @Param("entity") UserTaxnoEntity t);

    /**
     * 根据id删除用户信息
     * @param id
     * @return
     */
    int delete(@Param("schemaLabel") String schemaLabel, @Param("id") Long id);

    /**
     * 删除 - 根据用户
     */
    void deleteByUserId(@Param("schemaLabel")String schemaLabel,@Param("userId") Long userId);

    /**
     * 根据条件查询用户税号信息
     * @param query
     * @return
     */
    List<UserTaxnoEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("entity") UserTaxnoEntity query);

    /**
     * 根据条件查询用户税号信息总数
     * @param query
     * @return
     */
    int queryTotal(@Param("schemaLabel") String schemaLabel, @Param("entity") UserTaxnoEntity query);
}
