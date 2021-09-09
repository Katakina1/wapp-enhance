package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.base.entity.MenuEntity;

import java.util.List;


/**
 * 菜单管理
 * <p>
 * Created by Daily.zhang on 2018/04/13.
 */
public interface MenuService {

    /**
     * 根据父菜单，查询子菜单
     *
     * @param parentId 父菜单ID
     */
    List<MenuEntity> queryListParentId(String schemaLabel, Long parentId);

    /**
     * 获取用户菜单
     *
     * @param userId 用户id
     * @param type   0-后台用 1-前台用
     * @return
     */
    List<MenuEntity> getUserMenuList(String schemaLabel, Long userId, Integer type);

    /**
     * 获取用户菜单(默认后台用)
     *
     * @param userId 用户id
     * @return
     */
    List<MenuEntity> getUserMenuList(String schemaLabel, Long userId);

    /**
     * 查询菜单
     */
    MenuEntity queryObject(String schemaLabel, Long menuId);

    /**
     * 查询菜单列表
     */
    List<MenuEntity> queryList(String schemaLabel, MenuEntity entity);

    /**
     * 查询总数
     */
    int queryTotal(String schemaLabel, MenuEntity entity);

    /**
     * 保存菜单
     */
    void save(String schemaLabel, MenuEntity entity);

    /**
     * 修改
     */
    void update(String schemaLabel, MenuEntity entity);

    /**
     * 删除
     */
    void delete(String schemaLabel, Long menuId);

    /**
     * 批量删除
     */
    int deleteBatch(String schemaLabel, Long[] menuIds);

}
