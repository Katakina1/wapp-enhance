package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.UserRoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/17.
 */
@Mapper
public interface UserRoleDao {

    /**
     * 保存用户角色关联信息
     *
     * @param t
     */
    int save(@Param("schemaLabel") String schemaLabel, @Param("entity") UserRoleEntity t);

    /**
     * 根据用户id删除关联相信
     *
     * @return
     */
    int delete(@Param("schemaLabel") String schemaLabel, @Param("entity") UserRoleEntity entity);

    /**
     * 根据条件查询关联信息
     *
     * @param query
     * @return
     */
    List<UserRoleEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("entity") UserRoleEntity query);

    /**
     * 根据条件查询关联信息总数
     *
     * @param query
     * @return
     */
    int queryTotal(@Param("schemaLabel") String schemaLabel, @Param("entity") UserRoleEntity query);

    /**
     * 根据用户ID，获取角色ID列表
     */
    List<Long> queryRoleIdList(@Param("schemaLabel") String schemaLabel, @Param("userId") Long userId);

    int saveRoleUser(@Param("schemaLabel") String schemaLabel, @Param("entity") UserRoleEntity entity);
}
