package com.xforceplus.wapp.modules.sys.service;

import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.entity.UserTokenEntity;

/**
 * shiro相关接口
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-06-06 8:49
 */
public interface ShiroService {

    /**
     * 查询UserTokenEntity
     *
     * @param schemaLabel
     * @param token
     * @return
     */
    UserTokenEntity queryByToken(String schemaLabel, String token);

    /**
     * 根据用户ID，查询用户
     *
     * @param userId
     */
    UserEntity queryUser(String schemaLabel, Long userId);

    /**
     * 更新UserTokenEntity
     *
     * @param schemaLabel
     * @param token
     */
    int updateToken(String schemaLabel, UserTokenEntity token);
}
