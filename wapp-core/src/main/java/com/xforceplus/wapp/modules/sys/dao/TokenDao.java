package com.xforceplus.wapp.modules.sys.dao;

import com.xforceplus.wapp.modules.sys.entity.TokenEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Token
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-23 15:22:07
 */
@Mapper
public interface TokenDao {

    TokenEntity queryByUserId(Long userId);

    TokenEntity queryByToken(String token);

    void save(TokenEntity entity);

    int update(TokenEntity t);

}
