package com.xforceplus.wapp.modules.sys.service.impl;

import com.xforceplus.wapp.modules.base.dao.BaseUserDao;
import com.xforceplus.wapp.modules.base.dao.UserTokenDao;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.entity.UserTokenEntity;
import com.xforceplus.wapp.modules.sys.service.ShiroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShiroServiceImpl implements ShiroService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroServiceImpl.class);

    private final BaseUserDao baseUserDao;

    private final UserTokenDao userTokenDao;

    @Autowired
    public ShiroServiceImpl(UserTokenDao userTokenDao, BaseUserDao baseUserDao) {
        this.userTokenDao = userTokenDao;
        this.baseUserDao = baseUserDao;
    }

    @Override
    public UserTokenEntity queryByToken(String schemaLabel, String token) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Shiro查询Token信息, Token:{}, 分库:{}", token, schemaLabel);
        }

        return userTokenDao.queryByToken(schemaLabel, token);
    }

    @Override
    public UserEntity queryUser(String schemaLabel, Long userId) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Shiro查询User信息, UserId:{}, 分库:{}", userId, schemaLabel);
        }

        return baseUserDao.queryObject(schemaLabel, userId);
    }

    @Override
    public int updateToken(String schemaLabel, UserTokenEntity token) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Shiro更新Token信息, Token:{}, 分库:{}", token, schemaLabel);
        }

        return userTokenDao.updateToken(schemaLabel, token);
    }
}
