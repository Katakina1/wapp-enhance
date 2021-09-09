package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.base.entity.UserRoleEntity;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/17.
 */
public interface UserRoleService {

    /**
     * 根据条件查询用户角色信息
     * @param entity
     * @return
     */
    List<UserRoleEntity> queryList(String schemaLabel, UserRoleEntity entity);

    /**
     * 保存用户角色信息
     * 角色信息默认从entity.roleIds取值，如为null从entity.roleid取值
     * @param entity
     */
    void save(String schemaLabel, UserRoleEntity entity);

    /**
     * 根据用户id删除角色关联信息
     */
    void delete(String schemaLabel, UserRoleEntity entity);

    /**
     * 删除原用户关联的角色信息，然后再添加新的用户角色关联信息
     * @param entity
     */
    void saveOrUpdate(String schemaLabel, UserRoleEntity entity);

    /**
     * 根据用户ID，获取角色ID列表
     */
    List<Long> queryRoleIdList(String schemaLabel, Long userId);

    /**
     * 查询条件相应的数据总数
     * @param entity
     * @return
     */
    int queryTotal(String schemaLabel, UserRoleEntity entity);

    /**
     * 角色用户关联
     */
    void saveRoleUser(String schemaLabel, UserRoleEntity entity);
}
