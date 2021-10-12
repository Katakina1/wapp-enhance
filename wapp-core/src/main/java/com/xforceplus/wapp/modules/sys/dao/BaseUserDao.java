package com.xforceplus.wapp.modules.sys.dao;

import com.xforceplus.wapp.modules.sys.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/17.
 */
@Mapper
public interface BaseUserDao {

    /**
     * 根据userId查用户
     *
     * @param userId
     * @return
     */
    UserEntity queryObject(@Param("schemaLabel") String schemaLabel, @Param("userId") Long userId);

    /**
     * 根据条件查询用户
     */
    List<UserEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("entity") UserEntity query);

    /**
     * 查询用户的所有菜单ID
     */
    List<Long> queryAllMenuId(@Param("schemaLabel") String schemaLabel, @Param("userId") Long userId);

    /**
     * 根据用户名，查询系统用户
     */
    UserEntity queryByUserName(@Param("schemaLabel") String schemaLabel, @Param("username") String username);

    /**
     * 保存用户信息
     *
     * @param query
     */
    int save(@Param("schemaLabel") String schemaLabel, @Param("entity") UserEntity query);

    /**
     * 更新用户信息
     */
    int update(@Param("schemaLabel") String schemaLabel, @Param("entity") UserEntity query);

    /**
     * 删除用户信息
     */
    int delete(@Param("schemaLabel") String schemaLabel, @Param("userId") Long userId);

    /**
     * 修改用户密码
     */
    int updatePassword(@Param("schemaLabel") String schemaLabel, @Param("userId") Long userid, @Param("password") String oldPW, @Param("newPassword") String newPW, @Param("encodePassword") String enodePW);

    /**
     * 批量删除用户信息
     */
    int deleteBatch(@Param("schemaLabel") String schemaLabel, @Param("userids") Long[] ids);

    /**
     * 查询用户汇总信息
     */
    int queryTotal(@Param("schemaLabel") String schemaLabel, @Param("entity") UserEntity query);

    /**
     * 统计组织下的用户数量
     *
     * @param orgIds 机构id
     * @return 统计结果
     */
    int userTotal(@Param("schemaLabel")String schemaLabel,@Param("orgIds") Long[] orgIds);

    /**
     * 数据权限管理，查询用户汇总信息
     */
    int queryDataAccessTotal(@Param("schemaLabel") String schemaLabel, @Param("entity") UserEntity query);

    /**
     * 数据权限管理,根据条件查询用户
     */
    List<UserEntity> queryDataAccessList(@Param("schemaLabel") String schemaLabel, @Param("entity") UserEntity query);

    /**
     * 获取登陆人orgtype
     * @param userid
     * @return
     */
    String getOrgtype(Long userid);

    /**
     * 获取登陆人orgtype
     * @param userid
     * @return
     */
    String getOrgtypes(Long userid);
}
