package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.base.entity.UserEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by Daily.zhang on 2018/04/17.
 *
 * BaseUserService
 */
public interface BaseUserService {
    /**
     * 查询用户的所有菜单ID
     */
    List<Long> queryAllMenuId(String schemaLabel, Long userId);

    /**
     * 根据用户名，查询系统用户
     */
    UserEntity queryByUserName(String schemaLabel, String username);

    /**
     * 根据用户ID，查询用户
     *
     * @param userId
     * @return
     */
    UserEntity queryObject(String schemaLabel, Long userId);

    /**
     * 查询用户列表
     */
    List<UserEntity> queryList(String schemaLabel, UserEntity entity);

    /**
     * 查询总数
     */
    int queryTotal(String schemaLabel, UserEntity entity);

    /**
     * 统计组织下的用户数量
     *
     * @param orgIds 机构id
     * @return 统计结果
     */
    int userTotal(String schemaLabel, Long[] orgIds);

    /**
     * 保存用户
     */
    Map<String, Object> save(String schemaLabel, UserEntity user);

    /**
     * 修改用户
     */
    Map<String, Object> update(String schemaLabel, UserEntity user);

    /**
     * 删除用户
     */
    int delete(String schemaLabel, Long userid);

    /**
     * 修改用户密码
     */
    int modifyPassword(String schemaLabel, Long userid, String oldPW, String newPW);

    /**
     *数据权限管理，查询用户列表
     */
    List<UserEntity> queryDataAccessList(String schemaLabel, UserEntity userEntity);

    /**
     *数据权限管理，查询总数
     */
    int queryDataAccessTotal(String schemaLabel, UserEntity userEntity);
}
