package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.RoleMenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单关联
 *
 * Created by Daily.zhang on 2018/04/16.
 */
@Mapper
public interface RoleMenuDao {

    /**
     * 保存角色菜单关联关系
     * menuIdList 字段存入多个菜单id可以一次性批量保存多个关联关系
     * @param entity
     */
    int save(@Param("schemaLabel") String schemaLabel, @Param("entity") RoleMenuEntity entity);

    /**
     * 更新角色菜单信息
     * @param entity
     * @return
     */
    int update(@Param("schemaLabel") String schemaLabel, @Param("entity") RoleMenuEntity entity);

    /**
     * 根据角色id删除关联关系
     * @param roleid
     * @return
     */
    int delete(@Param("schemaLabel") String schemaLabel, @Param("roleid") Long roleid);

    /**
     * 根据条件查询角色菜单关联信息
     * @param query
     * @return
     */
    List<RoleMenuEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("entity") RoleMenuEntity query);

    /**
     * 根据条件查询角色菜单关联信息总数
     * @param query
     * @return
     */
    int queryTotal(@Param("schemaLabel") String schemaLabel, @Param("entity") RoleMenuEntity query);

    /**
     * 根据角色ID，获取菜单ID列表
     */
    List<Long> queryMenuIdList(@Param("schemaLabel") String schemaLabel, @Param("roleid") Long roleid);
}
