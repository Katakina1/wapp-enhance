package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.base.entity.RoleMenuEntity;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/16.
 */
public interface RoleMenuService {

    /**
     * 根据条件查询角色菜单信息
     *
     * @param schemaLabel
     * @param entity
     * @return
     */
    List<RoleMenuEntity> queryList(String schemaLabel, RoleMenuEntity entity);

    /**
     * 保存角色菜单信息
     * 菜单信息默认从entity.menuIdList取值，如为null从entity.menuid取值
     *
     * @param entity
     */
    void save(String schemaLabel, RoleMenuEntity entity);

    /**
     * 删除角色相关的菜单信息
     *
     * @param roleid
     */
    void delete(String schemaLabel, Long roleid);

    /**
     * 删除原角色相关的菜单信息，然后再添加相关信息
     * entity.roleid
     * entity.menuIdList
     *
     * @param entity
     */
    void saveOrUpdate(String schemaLabel, RoleMenuEntity entity);

    /**
     * 根据角色ID，获取菜单ID列表
     */
    List<Long> queryMenuIdList(String schemaLabel, Long roleId);

    /**
     * 根据条件查询角色菜单记录数
     *
     * @param schemaLabel
     * @param entity
     * @return
     */
    int queryTotal(String schemaLabel, RoleMenuEntity entity);

}
