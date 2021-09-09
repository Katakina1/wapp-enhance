package com.xforceplus.wapp.modules.api.dao;

import com.xforceplus.wapp.modules.api.entity.TokenEntity;
import com.xforceplus.wapp.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Token
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-23 15:22:07
 */
@Mapper
public interface TokenDao extends BaseDao<TokenEntity> {

    TokenEntity queryByUserId(Long userId);

    TokenEntity queryByToken(String token);

}
