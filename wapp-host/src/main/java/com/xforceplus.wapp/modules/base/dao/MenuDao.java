package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.MenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单管理
 * <p>
 * Created by Daily.zhang on 2018/04/13.
 */
@Mapper
public interface MenuDao{

    /**
     * 根据菜单id查询菜单
     *
     * @param menuId
     * @return
     */
    MenuEntity queryObject(@Param("schemaLabel") String schemaLabel, @Param("menuId") Long menuId);

    /**
     * 保存菜单信息
     */
    int save(@Param("schemaLabel") String schemaLabel, @Param("entity") MenuEntity entity);

    /**
     * 根据父菜单，查询子菜单
     *
     * @param parentId 父菜单ID
     */
    List<MenuEntity> queryListParentId(@Param("schemaLabel") String schemaLabel, @Param("parentId") Long parentId);

    /**
     * 根据条件查询菜单信息
     *
     * @param query
     * @return
     */
    List<MenuEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("entity") MenuEntity query);

    /**
     * 根据条件查询菜单数
     */
    int queryTotal(@Param("schemaLabel") String schemaLabel, @Param("entity") MenuEntity query);

    /**
     * 更新菜单信息
     */
    int update(@Param("schemaLabel") String schemaLabel, @Param("entity") MenuEntity query);

    /**
     * 获取用户菜单
     *
     * @param userId 用户id
     * @return
     */
    List<MenuEntity> getUserMenuList(@Param("schemaLabel") String schemaLabel, @Param("userId") Long userId);

    /**
     * 获取用户菜单(子菜单)
     *
     * @param userId   用户id
     * @param parentId 父菜单ID
     * @return
     */
    List<MenuEntity> getUserMenuListOfParentId(@Param("schemaLabel") String schemaLabel, @Param("userId") Long userId, @Param("parentId") Long parentId);

    Integer updateBottom(@Param("schemaLabel") String schemaLabel, @Param("menuId") Integer menuId);

    /**
     * 根据菜单Id删除菜单
     *
     * @param menuId
     * @return
     */
    int delete(@Param("schemaLabel") String schemaLabel, @Param("menuId") Long menuId);

    /**
     * 批量删除菜单
     *
     * @param ids
     * @return
     */
    int deleteBatch(@Param("schemaLabel") String schemaLabel, @Param("ids") Long[] ids);
}
