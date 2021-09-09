package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.UserTokenEntity;

/**
 * 用户Token
 * <p>
 * Created by Daily.zhang on 2018/04/24
 */
public interface UserTokenService {

    UserTokenEntity queryByUserId(String schemaLabel, Long userId);

    UserTokenEntity queryByToken(String schemaLabel, String token);

    void save(String schemaLabel, UserTokenEntity token);

    void update(String schemaLabel, UserTokenEntity token);

    /**
     * 生成token
     *
     * @param userId 用户ID
     */
    R createToken(String schemaLabel, long userId);

}
