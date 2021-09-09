package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.base.entity.RoleEntity;

import java.util.List;


/**
 * 角色
 * <p>
 * Created by Daily.zhang on 2018/04/13.
 */
public interface RoleService {

    /**
     * 根据角色id查询角色信息
     *
     * @param roleId
     * @return
     */
    RoleEntity queryObject(String schemaLabel, Long roleId);

    /**
     * 根据条件查询角色信息
     *
     * @param entity
     * @return
     */
    List<RoleEntity> queryList(String schemaLabel, RoleEntity entity);

    /**
     * 根据条件查询角色记录数
     *
     * @param entity
     * @return
     */
    int queryTotal(String schemaLabel, RoleEntity entity);

    /**
     * 统计组织下的角色数量
     *
     * @param orgIds 机构id
     * @return 统计结果
     */
    int roleTotal(String schemaLabel, Long[] orgIds);

    /**
     * 保存角色信息
     *
     * @param entity
     */
    void save(String schemaLabel, RoleEntity entity);

    /**
     * 更新角色信息
     *
     * @param entity
     */
    void update(String schemaLabel, RoleEntity entity);

    /**
     * 根据角色id删除角色信息
     *
     * @param roleid
     */
    void delete(String schemaLabel, Long roleid);

    /**
     * 批量删除
     */
    int deleteBatch(String schemaLabel, Long[] roleids);
}
