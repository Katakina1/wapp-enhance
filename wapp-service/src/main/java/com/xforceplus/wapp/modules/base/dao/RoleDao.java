package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.RoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色管理
 *
 * Created by Daily.zhang on 2018/04/13.
 */
@Mapper
public interface RoleDao{

    /**
     * 保存角色信息
     * @param entity
     */
    int save(@Param("schemaLabel") String schemaLabel, @Param("entity") RoleEntity entity);

    /**
     * 更新角色信息
     * @param entity
     * @return
     */
    int update(@Param("schemaLabel") String schemaLabel, @Param("entity") RoleEntity entity);

    /**
     * 根据角色id删除角色
     * @param roleid
     * @return
     */
    int delete(@Param("schemaLabel") String schemaLabel, @Param("roleid") Long roleid);

    /**
     * 批量删除角色
     * @param ids
     * @return
     */
    int deleteBatch(@Param("schemaLabel") String schemaLabel, @Param("ids") Long[] ids);

    /**
     *根据角色id查角色信息
     * @param id
     * @return
     */
    RoleEntity queryObject(@Param("schemaLabel") String schemaLabel, @Param("roleid") Long id);

    /**
     * 根据条件查角色信息
     * @param query
     * @return
     */
    List<RoleEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("entity") RoleEntity query);

    /**
     * 根据条件查角色总数
     * @param query
     * @return
     */
    int queryTotal(@Param("schemaLabel") String schemaLabel, @Param("entity") RoleEntity query);

    /**
     * 统计组织下的角色数量
     *
     * @param orgIds 机构id
     * @return 统计结果
     */
    int roleTotal(@Param("schemaLabel")String schemaLabel,@Param("orgIds") Long[] orgIds);

}
