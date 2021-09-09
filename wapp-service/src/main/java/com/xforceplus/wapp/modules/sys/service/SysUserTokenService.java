package com.xforceplus.wapp.modules.sys.service;

import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.sys.entity.SysUserTokenEntity;

/**
 * 用户Token
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-23 15:22:07
 */
public interface SysUserTokenService {

    SysUserTokenEntity queryByUserId(String schemaLabel, Long userId);

    SysUserTokenEntity queryByToken(String schemaLabel, String token);

    void save(String schemaLabel, SysUserTokenEntity token);

    void update(String schemaLabel, SysUserTokenEntity token);

    /**
     * 生成token
     *
     * @param userId 用户ID
     */
    R createToken(String schemaLabel, long userId);

}
